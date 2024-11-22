package com.sjtzooi.templatelibrary_nosql;

import lombok.extern.slf4j.Slf4j;
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

    @Autowired
    DocumentModelService documentModelService;

    @PostMapping("/add")
    public ResponseEntity<String> AddDocumentModel(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {
            log.error("File is empty");
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            String fileKey = documentModelService.addDocumentModel(file);
            return ResponseEntity.ok(fileKey);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }
    @GetMapping("/get/{fileKey}")
    public ResponseEntity getFile(@PathVariable("fileKey") String fileKey) throws IOException {
        DocumentModel model = documentModelService.getDocumentModel(fileKey);
        if (model == null) {
            return ResponseEntity.badRequest().body("No document model found with the given file key.");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + model.getFileName()+ "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(model.getFileSize()))
                .body(model.getFileData());
    }

}
