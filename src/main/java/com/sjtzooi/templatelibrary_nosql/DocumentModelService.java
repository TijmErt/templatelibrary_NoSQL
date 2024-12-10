package com.sjtzooi.templatelibrary_nosql;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

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
            metaData.put("fileSize", file.getSize());
            metaData.put("uploadDate", LocalDate.now().toString());

            ObjectId id = gridFsTemplate.store(
                    file.getInputStream(), file.getName(), file.getContentType(), metaData);
            log.info(id.toString());
            return id.toString();
        }
        catch(Exception e){
         log.error(ERROR_MESSAGE_SERVICE_LAYER+" addDocumentModel: {} ",e.getMessage());
         throw e;
        }
    }

    public DocumentModel getDocumentModel(String id) throws IOException {
        try{
            GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
            DocumentModel documentModel = new DocumentModel();
            Document metadata = file.getMetadata();
            documentModel.setFileSize(Long.parseLong(metadata.get("fileSize").toString()));
            documentModel.setUploadDate(LocalDate.parse(metadata.get("uploadDate").toString()));
            documentModel.setFileName(operations.getResource(file).getFilename());
            documentModel.setContentType(operations.getResource(file).getContentType());
            documentModel.setFileData( new InputStreamResource(operations.getResource(file).getInputStream()));
            documentModel.setFileKey(id);
            return documentModel;
        }
        catch(Exception e){
            log.error(ERROR_MESSAGE_SERVICE_LAYER +" getDocumentModel: {} ", e.getMessage());
            throw e;
        }

    }
}
