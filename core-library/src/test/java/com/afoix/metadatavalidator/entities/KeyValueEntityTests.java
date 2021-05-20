package com.afoix.metadatavalidator.entities;

import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class KeyValueEntityTests {
    @Test
    public void hasAttribute_whenAttributeIsPresent_returnsTrue() throws InvalidAttributeNameOrPathException {
        KeyValueEntity entity = makeEntity("my element", true);
        assertThat(entity.hasAttribute("my element")).isTrue();
    }

    @Test
    public void hasAttribute_whenAttributeIsNotPresent_returnsFalse() throws InvalidAttributeNameOrPathException {
        KeyValueEntity entity = makeEntity("my element", true);
        assertThat(entity.hasAttribute("notPresentElement")).isFalse();
    }

    @Test
    public void getAttributeValues_whenAttributeHasValue_returnsAttributeValue() throws InvalidAttributeNameOrPathException {
        KeyValueEntity entity = makeEntity("my element", "content");
        assertThat(entity.getAttributeValues("my element")).singleElement().isEqualTo("content");
    }

    @Test
    public void getAttributeValues_whenAttributeHasMultipleValues_returnsAllValues() throws InvalidAttributeNameOrPathException {
        KeyValueEntity entity = makeEntity("my element", "content1", "my element", "content2");
        assertThat(entity.getAttributeValues("my element")).containsExactly("content1", "content2");
    }

    @Test
    public void getAttributeValues_whenAttributeHasNoContent_returnsNullValue() throws InvalidAttributeNameOrPathException {
        KeyValueEntity entity = makeEntity("my element", null);
        assertThat(entity.getAttributeValues("my element")).singleElement().isNull();
    }

    @NotNull
    private KeyValueEntity makeEntity(Object... inputs) {
        KeyValueEntity result = new KeyValueEntity();
        for (int i = 0; i < inputs.length; i += 2) {
            result.putAdditionally((String)inputs[i], inputs[i+1]);
        }
        return result;
    }
}
