package com.afoix.metadatavalidator.entities;

import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Execution(ExecutionMode.CONCURRENT)
public class XMLEntityTests {

    @Test
    public void hasAttribute_whenAttributeIsPresent_returnsTrue() throws ParserConfigurationException, IOException, SAXException, InvalidAttributeNameOrPathException {
        XMLEntity entity = makeEntity("<entity><MyElement/></entity>");
        assertThat(entity.hasAttribute("MyElement")).isTrue();
    }

    @Test
    public void hasAttribute_whenAttributeIsNotPresent_returnsFalse() throws ParserConfigurationException, IOException, SAXException, InvalidAttributeNameOrPathException {
        XMLEntity entity = makeEntity("<entity><MyElement/></entity>");
        assertThat(entity.hasAttribute("NotPresentElement")).isFalse();
    }

    @Test
    public void getAttributeValues_whenElementHasContent_returnsElementContent() throws ParserConfigurationException, IOException, SAXException, InvalidAttributeNameOrPathException {
        XMLEntity entity = makeEntity("<entity><MyElement>content</MyElement></entity>");
        assertThat(entity.getAttributeValues("MyElement")).singleElement().isEqualTo("content");
    }

    @Test
    public void getAttributeValues_whenMultipleElementsHaveContent_returnsAllElementContent() throws ParserConfigurationException, IOException, SAXException, InvalidAttributeNameOrPathException {
        XMLEntity entity = makeEntity("<entity><MyElement>content1</MyElement><MyElement>content2</MyElement></entity>");
        assertThat(entity.getAttributeValues("MyElement")).containsExactly("content1", "content2");
    }

    @Test
    public void getAttributeValues_whenElementHasNoContent_returnsNullValue() throws ParserConfigurationException, IOException, SAXException, InvalidAttributeNameOrPathException {
        XMLEntity entity = makeEntity("<entity><MyElement/></entity>");
        assertThat(entity.getAttributeValues("MyElement")).singleElement().isNull();
    }

    @Test
    public void caseInsensitiveIsNotSupported() throws ParserConfigurationException, IOException, SAXException {
        XMLEntity entity = makeEntity("<entity><MyElement/></entity>");
        assertThatThrownBy(() -> entity.setCaseSensitive(false)).isInstanceOf(Exception.class);
    }

    @Test
    public void invalidXPathExpression_throwsInvalidAttributeNameOrPathException() throws IOException, ParserConfigurationException, SAXException {
        XMLEntity entity = makeEntity("<entity><MyElement/></entity>");
        assertThatThrownBy(() -> entity.hasAttribute("")).isInstanceOf(InvalidAttributeNameOrPathException.class);
    }

    @NotNull
    private XMLEntity makeEntity(String xml) throws SAXException, IOException, ParserConfigurationException {
        Document document;

        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))){
            document = DocumentBuilderFactory.newDefaultInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);
        }

        return new XMLEntity(document.getDocumentElement());
    }
}
