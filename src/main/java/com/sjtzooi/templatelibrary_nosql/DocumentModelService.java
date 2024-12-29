package com.sjtzooi.templatelibrary_nosql;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.zip.GZIPOutputStream;

@Slf4j
@Service
public class DocumentModelService {

    private static final String ERROR_MESSAGE_SERVICE_LAYER= "DocumentModelService:";
    private GridFsTemplate gridFsTemplate;

    private GridFsOperations operations;

    @Autowired
    public DocumentModelService(GridFsTemplate gridFsTemplate, GridFsOperations operations) {
        this.gridFsTemplate = gridFsTemplate;
        this.operations = operations;
    }

    public String addDocumentModel(MultipartFile file) throws IOException {
        try{
            DBObject metaData = new BasicDBObject();
            metaData.put("uploadDate", LocalDate.now().toString());

            ObjectId id = gridFsTemplate.store(
                    file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metaData);
            return id.toString();
        }
        catch(Exception e){
         log.error(ERROR_MESSAGE_SERVICE_LAYER+" addDocumentModel: {} ",e.getMessage());
         throw e;
        }
    }

    public DocumentModel getUncompressedDocumentModel(ObjectId id) throws IOException {
        try{
            return getDocumentModel(id);
        }
        catch(Exception e){
            log.error(ERROR_MESSAGE_SERVICE_LAYER +" getUncompressedDocumentModel: {} ", e.getMessage());
            throw e;
        }
    }

    public DocumentModel getCompressedDocumentModel(ObjectId id) throws IOException {
        try {
            DocumentModel documentModel = getDocumentModel(id);
            InputStream compressedInputStream = compressedInputStream(documentModel.getFileStream().getInputStream());
            log.info("we back after compression");
            documentModel.setFileStream(new InputStreamResource(compressedInputStream));
            return documentModel;
        } catch (IOException e) {
            log.error(ERROR_MESSAGE_SERVICE_LAYER +" getCompressedDocumentModel: {}", e.getMessage());
            throw e;
        }
    }


    private DocumentModel getDocumentModel(ObjectId id) throws IOException {
        try{
            GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
            GridFsResource tempFile = operations.getResource(file);
            DocumentModel documentModel = new DocumentModel();
            LocalDate uploadDate = LocalDate.parse(file.getMetadata().get("uploadDate").toString());
            documentModel.setFileSize(tempFile.getGridFSFile().getLength());
            documentModel.setUploadDate(uploadDate);
            documentModel.setFileName(tempFile.getFilename());
            documentModel.setContentType(tempFile.getContentType());
            documentModel.setFileStream( new InputStreamResource(tempFile.getInputStream()));
            documentModel.setFileKey(id);
            return documentModel;
        }
        catch(IOException e) {
            log.error(ERROR_MESSAGE_SERVICE_LAYER +" getDocumentModel: {}", e.getMessage());
            throw e;
        }
    }

    private InputStream compressedInputStream(InputStream inputStream) throws IOException {
        // Create a ByteArrayOutputStream to hold the compressed data
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Set a larger buffer size (e.g., 8192 bytes)
        byte[] buffer = new byte[8192];

        // Wrap the ByteArrayOutputStream with GZIPOutputStream
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream,  true);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {

            int length;
            // Read the input stream in chunks and write to the GZIPOutputStream
            while ((length = bufferedInputStream.read(buffer)) > 0) {
                gzipOutputStream.write(buffer, 0, length);
            }
            gzipOutputStream.finish();  // Ensure data is written out to byteArrayOutputStream
        }

        // Return the compressed data as an InputStream
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

}
