package com.sjtzooi.templatelibrary_nosql;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.annotation.Id;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;

@Data
public class DocumentModel {
    @Id
    private ObjectId fileKey;
    private String fileName;
    private String contentType;
    private LocalDate uploadDate;

    private long fileSize;
    private InputStreamResource fileStream;

}
