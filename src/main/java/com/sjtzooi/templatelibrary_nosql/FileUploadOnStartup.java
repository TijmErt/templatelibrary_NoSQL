package com.sjtzooi.templatelibrary_nosql;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;


import java.io.InputStream;

import java.sql.Date;
import java.time.LocalDate;


@Component
@Slf4j

public class FileUploadOnStartup {

    @Autowired
    private GridFsTemplate gridFsTemplate;
    // assuming you have a service that provides file
    private LocalDate date;

    @PostConstruct
    public void run() throws Exception {
        try{
            log.info("Starting file upload");

            date = LocalDate.now();
            EmptyMonoDB();
            String idPDF = intilizatePDF("5fbc1c8475fd2f2ba84e6c2d");
            String idDOCX = intilizateDOCX("1f3f1e4ba6aa4c509c0ce162");

            log.info(idPDF.toString());
            log.info(idDOCX.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private void EmptyMonoDB(){
        gridFsTemplate.delete(new Query());
    }

    private String intilizatePDF(String hexString){
        try{
            Document metaDataPDF = new Document();
            metaDataPDF.put("uploadDate", date.toString());
            metaDataPDF.put("_contentType", "application/pdf");
            metaDataPDF.put("_class", "com.mongodb.BasicDBObject");

            ObjectId objectIdPDF = new ObjectId(hexString);
            BsonValue bsonValueIdPDF = new BsonObjectId(objectIdPDF);

            if (gridFsTemplate.find(new Query().addCriteria(Criteria.where("_id").is(objectIdPDF))).iterator().hasNext()) {
                gridFsTemplate.delete(new Query().addCriteria(Criteria.where("_id").is(objectIdPDF)));
                log.info("Existing PDF file with ObjectId " + objectIdPDF.toString() + " removed.");
            }

            Resource resourcePDF = new ClassPathResource("files/Research Rapport Data Preview.pdf");
            if (!resourcePDF.exists()) {
                return "File not found: " + resourcePDF.getFilename();
            }
            InputStream fileInputStreamPDF = resourcePDF.getInputStream();
            String filenamePDF = resourcePDF.getFilename();  // Get the file name
            long fileSizePDF = resourcePDF.contentLength();

            GridFSFile gridFSFilePDF = new GridFSFile(bsonValueIdPDF, filenamePDF, fileSizePDF, 261120, Date.valueOf(date), metaDataPDF);
            GridFsResource gridFsResourcePDF = new GridFsResource(gridFSFilePDF, fileInputStreamPDF);
            var IdPDF= gridFsTemplate.store(gridFsResourcePDF).toString();
            return IdPDF.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private String intilizateDOCX(String hexString){


        try {
            Document metaDataDOCX= new Document();
            metaDataDOCX.put("uploadDate", date.toString());
            metaDataDOCX.put("_contentType", "application/application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            metaDataDOCX.put("_class", "com.mongodb.BasicDBObject");

            ObjectId objectIdDOCX = new ObjectId(hexString);
            BsonValue bsonValueIdDOCX  = new BsonObjectId(objectIdDOCX);


            if (gridFsTemplate.find(new Query().addCriteria(Criteria.where("_id").is(objectIdDOCX))).iterator().hasNext()) {
                gridFsTemplate.delete(new Query().addCriteria(Criteria.where("_id").is(objectIdDOCX)));
                log.info("Existing DOCX file with ObjectId " + objectIdDOCX.toString() + " removed.");
            }


            Resource resourceDOCX = new ClassPathResource("files/Research Rapport Security.docx");
            if (!resourceDOCX.exists()) {
                return "File not found: " + resourceDOCX.getFilename();
            }

            InputStream fileInputStreamDOCX = resourceDOCX.getInputStream();
            String filenameDOCX = resourceDOCX.getFilename();  // Get the file name
            long fileSizeDOCX = resourceDOCX.contentLength();


            GridFSFile gridFSFileDOCX = new GridFSFile(bsonValueIdDOCX, filenameDOCX, fileSizeDOCX, 261120, Date.valueOf(date), metaDataDOCX);
            GridFsResource gridFsResourceDOCX = new GridFsResource(gridFSFileDOCX, fileInputStreamDOCX);


            var IdDOCX = gridFsTemplate.store(gridFsResourceDOCX);
            return IdDOCX.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

