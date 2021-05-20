package com.afoix.metadatavalidator.loaders;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.entities.JSONEntity;
import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JSONEntityLoaderTests {

    @Test
    public void singleObjectJson_producesSingleEntity() throws IOException {

        String jsonContent = "{\"key\":\"value\"}";

        JSONEntityLoader loader = new JSONEntityLoader(new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8)));
        Stream<JSONEntity> entities = loader.load();

        assertThat(entities).singleElement().satisfies(entity -> {
            try {
                assertThat(entity.hasAttribute("key")).isTrue();
                assertThat(entity.getAttributeValues("key")).singleElement().isEqualTo("value");
            } catch (InvalidAttributeNameOrPathException e) {
                Assertions.fail();
            }
        });
    }

    @Test
    public void arrayJson_producesMultipleEntities() throws IOException {

        String jsonContent = "[{\"key\":\"value\"}, {\"key2\":\"value2\"}]";

        JSONEntityLoader loader = new JSONEntityLoader(new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8)));
        Stream<JSONEntity> entities = loader.load();

        assertThat(entities).hasSize(2).satisfiesExactlyInAnyOrder(
                entity -> {
                    try {
                        assertThat(entity.hasAttribute("key")).isTrue();
                        assertThat(entity.getAttributeValues("key")).singleElement().isEqualTo("value");
                    } catch (InvalidAttributeNameOrPathException e) {
                        Assertions.fail();
                    }
                },
                entity -> {
                    try {
                        assertThat(entity.hasAttribute("key2")).isTrue();
                        assertThat(entity.getAttributeValues("key2")).singleElement().isEqualTo("value2");
                    } catch (InvalidAttributeNameOrPathException e) {
                        Assertions.fail();
                    }
                }
        );
    }

    @Test
    public void arrayJson_producesEntitiesWithUniqueIdentifiers() throws IOException {
        String jsonContent = "[{\"key\":\"value\"}, {\"key2\":\"value2\"}]";

        JSONEntityLoader loader = new JSONEntityLoader(new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8)));
        Stream<JSONEntity> entities = loader.load();

        assertThat(entities).extracting(Entity::getIdentifier).doesNotHaveDuplicates();
    }

    @Test
    public void otherKindOfJson_isNotSupported() {
        String jsonContent = "\"test\"";

        JSONEntityLoader loader = new JSONEntityLoader(new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8)));

        assertThatThrownBy(loader::load).isNotNull();
    }

}
