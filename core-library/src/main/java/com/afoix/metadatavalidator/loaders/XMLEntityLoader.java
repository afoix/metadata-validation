package com.afoix.metadatavalidator.loaders;

import com.afoix.metadatavalidator.entities.XMLEntity;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.List;
import java.util.stream.Stream;

public class XMLEntityLoader implements EntityLoader<XMLEntity> {

    private final InputStream inputStream;
    private String sourceName;
    private boolean ownsStream;

    public XMLEntityLoader(File sourceFile) throws FileNotFoundException {
        this(new FileInputStream(sourceFile));
        ownsStream = true;
        sourceName = sourceFile.getName();
    }

    public XMLEntityLoader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public XMLEntityLoader withSourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    @Override
    public Stream<XMLEntity> load() throws IOException, InvalidFormatException {
        try {
            DocumentBuilder builder;
            try {
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new InvalidOperationException("Failed to create document builder", e);
            }

            Document document;
            try {
                document = builder.parse(inputStream);

            } catch (SAXException e) {
                throw new InvalidFormatException("Invalid XML", e);
            }

            final XMLEntity entity = new XMLEntity(document.getDocumentElement());
            entity.setIdentifier(sourceName);
            return Stream.of(entity);
        } finally {
            if (ownsStream) {
                inputStream.close();
            }
        }
    }
}
