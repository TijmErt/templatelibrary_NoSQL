package com.sjtzooi.templatelibrary_nosql;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@CrossOrigin(origins ="*" )
@RequestMapping("/api/DocumentModelController")
public class DocumentModelController {


    private DocumentModelService documentModelService;

    @Autowired
    public DocumentModelController(DocumentModelService documentModelService) {
        this.documentModelService = documentModelService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> AddDocumentModel(@RequestParam("file") MultipartFile file) {
        try {
            if(file.isEmpty()) {
                log.error("File is empty");
                return ResponseEntity.badRequest().body("File is empty");
            }
            String fileKey = documentModelService.addDocumentModel(file);
            return ResponseEntity.ok(fileKey);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }
    @GetMapping("/get/{fileKey}")
    public ResponseEntity<?> getFileData(@PathVariable("fileKey") String fileKey) throws IOException {
        try {
            DocumentModel model = documentModelService.getUncompressedDocumentModel(new ObjectId(fileKey));
            if (model == null) {
                return ResponseEntity.badRequest().body("No document model found with the given file key.");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + model.getFileName()+ "\"")
                    .header(HttpHeaders.CONTENT_TYPE, model.getContentType() != null && !model.getContentType().isEmpty()
                            ? model.getContentType()
                            : "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(model.getFileSize()))
                    .body(model.getFileStream());
        }
        catch (Exception e) {
            log.debug(e.getMessage());
            return ResponseEntity.status(500).body("Error downloading the file: " + e.getMessage());
        }
    }

    @GetMapping("/get/{fileKey}/compressed")
    public ResponseEntity<?> getCompressedDocument(@PathVariable("fileKey") String fileKey) throws IOException {
        ObjectId id = new ObjectId(fileKey);
        DocumentModel model = documentModelService.getCompressedDocumentModel(id);

        // Return the compressed file with the correct headers
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + model.getFileName()+ "\"")
                .header(HttpHeaders.CONTENT_TYPE, model.getContentType() != null && !model.getContentType().isEmpty()
                        ? model.getContentType()
                        : "application/octet-stream")
                .header(HttpHeaders.CONTENT_ENCODING, "gzip")  // Indicating that the content is compressed with gzip
                .body(model.getFileStream());  // The compressed content as InputStreamResource
    }
    @GetMapping("/get/{fileKey}/pdf")
    public ResponseEntity<?> getFileDataPDF(@PathVariable("fileKey") String fileKey) throws IOException {
        try {
            DocumentModel model = documentModelService.getPDFDocumentModel(new ObjectId(fileKey));
            if (model == null) {
                return ResponseEntity.badRequest().body("No document model found with the given file key.");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(model.getFileSize()))
                    .body(model.getFileStream());
        }
        catch (Exception e) {
            log.debug(e.getMessage());
            return ResponseEntity.status(500).body("Error downloading the file: " + e.getMessage());
        }
    }
}
