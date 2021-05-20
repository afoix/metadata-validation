package com.afoix.metadatavalidator.loaders;

import com.afoix.metadatavalidator.entities.XMLEntity;
import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class XMLEntityLoaderTests {
    @Test
    public void xmlDocument_producesSingleEntity() throws IOException, InvalidFormatException {

        String jsonContent = "<key>value</key>";

        XMLEntityLoader loader = new XMLEntityLoader(new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8)));
        Stream<XMLEntity> entities = loader.load();

        assertThat(entities).singleElement().satisfies(entity -> {
            try {
                assertThat(entity.hasAttribute("/key")).isTrue();
                assertThat(entity.getAttributeValues("/key")).singleElement().isEqualTo("value");
            } catch (InvalidAttributeNameOrPathException exception) {
                Assertions.fail("Threw an exception", exception);
            }
        });
    }
}
