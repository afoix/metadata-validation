package com.afoix.metadatavalidator.entities;

import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Execution(ExecutionMode.CONCURRENT)
public class JSONEntityTests {

    @Test
    public void hasAttribute_whenAttributeIsPresent_returnsTrue() throws IOException, InvalidAttributeNameOrPathException {
        JSONEntity entity = makeEntity("{\"myElement\":\"content\"}");
        assertThat(entity.hasAttribute("myElement")).isTrue();
    }

    @Test
    public void hasAttribute_whenAttributeIsNotPresent_returnsFalse() throws IOException, InvalidAttributeNameOrPathException {
        JSONEntity entity = makeEntity("{\"myElement\":\"content\"}");
        assertThat(entity.hasAttribute("notPresentElement")).isFalse();
    }

    @Test
    public void getAttributeValues_whenAttributeHasValue_returnsAttributeValue() throws IOException {
        JSONEntity entity = makeEntity("{\"myElement\":\"content\"}");
        assertThat(entity.getAttributeValues("myElement")).singleElement().isEqualTo("content");
    }

    @Test
    public void getAttributeValues_whenAttributeHasNoValue_returnsNullValue() throws IOException {
        JSONEntity entity = makeEntity("{\"myElement\": null}");
        assertThat(entity.getAttributeValues("myElement")).singleElement().isNull();
    }

    @Test
    public void hasAttribute_withNestedElement() throws IOException, InvalidAttributeNameOrPathException {
        JSONEntity entity = makeEntity("{\"parent\": {\"child\": \"value\"}}");
        assertThat(entity.hasAttribute("/parent/child")).isTrue();
    }

    @Test
    public void hasAttribute_withNestedElement_andNonExistingParent_isFalse() throws IOException, InvalidAttributeNameOrPathException {
        JSONEntity entity = makeEntity("{\"parent\": {\"child\": \"value\"}}");
        assertThat(entity.hasAttribute("/nonExistingParent/child")).isFalse();
    }

    @Test
    public void getAttributeValues_withNestedElement() throws IOException {
        JSONEntity entity = makeEntity("{\"parent\": {\"child\": \"value\"}}");
        assertThat(entity.getAttributeValues("/parent/child")).singleElement().isEqualTo("value");
    }

    @Test
    public void caseInsensitiveIsNotSupported() throws IOException {
        JSONEntity entity = makeEntity("{\"myElement\":\"content\"}");
        assertThatThrownBy(() -> entity.setCaseSensitive(false)).isInstanceOf(Exception.class);
    }

    @Test
    public void invalidJsonPointerExpression_throwsInvalidAttributeNameOrPathException() throws IOException, ParserConfigurationException, SAXException {
        JSONEntity entity = makeEntity("{\"myElement\":\"content\"}");
        assertThatThrownBy(() -> entity.hasAttribute("///invalid")).isInstanceOf(InvalidAttributeNameOrPathException.class);
    }

    @NotNull
    private JSONEntity makeEntity(String json) throws IOException {
        JSONObject rootObj;
        try (InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            rootObj = (JSONObject) new JSONTokener(stream).nextValue();
        }

        return new JSONEntity(rootObj);
    }
}
