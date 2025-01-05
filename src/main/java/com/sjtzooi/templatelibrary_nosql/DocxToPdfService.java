package com.sjtzooi.templatelibrary_nosql;

import jakarta.xml.bind.JAXBException;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.fo.renderers.FORendererApacheFOP;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;


import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.io.*;

@Service
@Slf4j
public class DocxToPdfService {

    public InputStream convertDocxToPdf(InputStream inputStream) throws IOException, Docx4JException, FOPException, JAXBException {
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        log.info(inputStream.available() + " bytes available");
        try {
            String regex = ".*(calibri|camb|cour|arial|times|comic|georgia|impact|LSANS|pala|tahoma|trebuc|verdana|symbol|webdings|wingding).*";
            PhysicalFonts.setRegex(regex);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);


            FOSettings foSettings =Docx4J.createFOSettings();
            foSettings.setOpcPackage(wordMLPackage);

            Docx4J.toFO(foSettings, pdfOutputStream, Docx4J.FLAG_EXPORT_PREFER_NONXSL);
            log.info("Length of conversion " + pdfOutputStream.toByteArray().length);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Load DOCX file into WordprocessingMLPackage (docx4j model)
        byte[] pdfBytes = pdfOutputStream.toByteArray();
        log.info("PDF ByteArray length: " + pdfOutputStream.size());
        return new ByteArrayInputStream(pdfBytes);
    }

}
