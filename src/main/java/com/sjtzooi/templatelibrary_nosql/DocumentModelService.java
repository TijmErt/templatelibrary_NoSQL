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
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    public String addDocumentModel(MultipartFile file) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("fileSize", file.getSize());
        metaData.put("uploadDate", LocalDate.now().toString());

        ObjectId id = gridFsTemplate.store(
                file.getInputStream(), file.getName(), file.getContentType(), metaData);
        log.info(id.toString());
        return id.toString();
    }

    public DocumentModel getDocumentModel(String id) throws IOException {
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
    public InputStreamResource getFile(String id) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        InputStreamResource stream = new InputStreamResource(operations.getResource(file).getInputStream());
        return stream;
    }
}
