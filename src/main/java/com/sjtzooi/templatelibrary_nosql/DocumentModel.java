package com.sjtzooi.templatelibrary_nosql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;

@Data
public class DocumentModel {
    @Id
    private String fileKey;
    private String fileName;
    private String contentType;
    private LocalDate uploadDate;

    private long fileSize;
    private byte[] fileData;

}
