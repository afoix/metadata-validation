package com.afoix.metadatavalidator.loaders;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class XSLXEntityLoaderTests {

    private XLSXEntityLoader loader;

    @BeforeEach
    public void findTestResource() throws URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Path xslxPath = Path.of(Objects.requireNonNull(classLoader.getResource("testcases/faang_metadata_examples_macleod_horses_20160205.xlsx")).toURI());
        loader = new XLSXEntityLoader(xslxPath.toFile());
    }

    @Test
    public void loadFromXlsx_producesEntityForEachRow() throws IOException, InvalidFormatException {
        Stream<KeyValueEntity> entities = loader.load();

        assertThat(entities).hasSizeGreaterThan(1);
    }

    @Test
    public void worksheetFilter_isCalledForEverySheet() throws IOException, InvalidFormatException {
        Set<String> worksheetNames = new HashSet<>();
        loader.withWorksheetFilter(worksheetName -> {
                    worksheetNames.add(worksheetName);
                    return true;
        }).load();

        assertThat(worksheetNames).contains("person", "organization", "specimen", "animal", "submission");
    }

    @Test
    public void worksheetFilter_returnsFalse_doesNotGenerateEntitiesFromThatWorksheet() throws IOException, InvalidFormatException {
        Stream<KeyValueEntity> entities = loader
                .withWorksheetFilter(worksheetName -> !worksheetName.equals("specimen"))
                .load();

        assertThat(entities).noneSatisfy(entity -> {
            assertThat(entity.hasAttribute("Sample Name")).isTrue();
            assertThat(entity.getAttributeValues("Sample Name")).contains("S1");
        });
    }

    @Test
    public void rowPostprocessor_canAddAttributeToEntities() throws IOException, InvalidFormatException {
        Stream<KeyValueEntity> entities = loader
                .withRowPostprocessor(row -> row.add(new AbstractMap.SimpleEntry<>("test_key", "test_value")))
                .load();

        assertThat(entities).allSatisfy(entity -> {
            assertThat(entity.hasAttribute("test_key")).isTrue();
            assertThat(entity.getAttributeValues("test_key")).singleElement().isEqualTo("test_value");
        });
    }

    @Test
    public void rowPostprocessor_ifAllAttributesAreDeleted_doesNotProduceEntity() throws IOException, InvalidFormatException {
        Stream<KeyValueEntity> entities = loader
                .withRowPostprocessor(List::clear)
                .load();

        assertThat(entities).isEmpty();
    }

}
