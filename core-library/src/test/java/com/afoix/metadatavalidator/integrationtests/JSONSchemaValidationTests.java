package com.afoix.metadatavalidator.integrationtests;

import com.afoix.metadatavalidator.entities.JSONEntity;
import com.afoix.metadatavalidator.loaders.JSONEntityLoader;
import com.afoix.metadatavalidator.problems.AttributeIsMissingProblem;
import com.afoix.metadatavalidator.problems.JSONSchemaValidationProblem;
import com.afoix.metadatavalidator.validators.ConformsToJSONSchemaValidator;
import com.afoix.metadatavalidator.validators.TestValidationContext;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class JSONSchemaValidationTests {

    @NotNull
    private static Stream<Arguments> findTestCasesInResources(String testCasesPath) throws URISyntaxException, IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Path rootDir = Path.of(Objects.requireNonNull(loader.getResource(testCasesPath)).toURI());
        return Files.walk(rootDir)
                .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                .sorted()
                .map(path -> Arguments.of(Named.of(rootDir.relativize(path).toString(), path.toFile())));
    }

    private static Stream<Arguments> validJSONSchemaMetadataSamples() throws URISyntaxException, IOException {
        return findTestCasesInResources("testcases/jsonschema/regular");
    }

    private static Stream<Arguments> validJSONSchemaMetadataArrayWithLinksSamples() throws URISyntaxException, IOException {
        return findTestCasesInResources("testcases/jsonschema/array-with-links");
    }

    @ParameterizedTest
    @MethodSource("validJSONSchemaMetadataSamples")
    public void validMetadataWithJsonSchema(File jsonFile) throws IOException {

        List<JSONEntity> entities = new JSONEntityLoader(jsonFile).load().toList();

        TestValidationContext context = new TestValidationContext();

        Map<URL, ConformsToJSONSchemaValidator> schemas = new HashMap<>();
        for (JSONEntity entity :
                entities) {

            URL schemaURI = new URL(entity.getRoot().get("describedBy").toString());
            ConformsToJSONSchemaValidator schema = schemas.get(schemaURI);
            if (schema == null) {
                schema = new ConformsToJSONSchemaValidator(schemaURI);
                schemas.put(schemaURI, schema);
            }

            try {
                schema.validate(entity, context);
            } catch (ValidationException exception) {
                // The default message in the ValidationException message is not very useful so
                // make a more useful one out of the causing exceptions
                throw new RuntimeException(exception.getCausingExceptions()
                        .stream()
                        .map(ValidationException::getErrorMessage)
                        .collect(Collectors.joining(", ")),
                        exception);
            }
        }

        assertThat(context.getProblems()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("validJSONSchemaMetadataArrayWithLinksSamples")
    public void validMetadataWithJsonSchemaArrayWithLinks(File jsonFile) throws IOException {
        JSONArray json;
        try (InputStream stream = new FileInputStream(jsonFile)) {
            json = new JSONArray(new JSONTokener(stream));
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        JSONObject schemaJson;
        try (InputStream stream = loader.getResourceAsStream("testcases/jsonschema/array-with-links-schema.json")) {
            assert stream != null;
            schemaJson = new JSONObject(new JSONTokener(stream));
        }
        Schema schema = SchemaLoader.load(schemaJson);

        try {
            schema.validate(json);
        } catch (ValidationException exception) {
            // The default message in the ValidationException message is not very useful so
            // make a more useful one out of the causing exceptions
            throw new RuntimeException(exception.getCausingExceptions()
                    .stream()
                    .map(ValidationException::getErrorMessage)
                    .collect(Collectors.joining(", ")),
                    exception);
        }
    }

    @Test
    public void invalidMetadataWithJsonSchema() throws URISyntaxException, IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        File jsonFile = Path.of(Objects.requireNonNull(loader.getResource("testcases/jsonschema/invalid/Q4_DEMO-project_PRJNA248302_biomaterial_1.json")).toURI()).toFile();

        List<JSONEntity> entities = new JSONEntityLoader(jsonFile).load().toList();

        TestValidationContext context = new TestValidationContext();

        Map<URL, ConformsToJSONSchemaValidator> schemas = new HashMap<>();
        for (JSONEntity entity :
                entities) {

            URL schemaURI = new URL(entity.getRoot().get("describedBy").toString());
            ConformsToJSONSchemaValidator schema = schemas.get(schemaURI);
            if (schema == null) {
                schema = new ConformsToJSONSchemaValidator(schemaURI);
                schemas.put(schemaURI, schema);
            }

            try {
                schema.validate(entity, context);
            } catch (ValidationException exception) {
                // The default message in the ValidationException message is not very useful so
                // make a more useful one out of the causing exceptions
                throw new RuntimeException(exception.getCausingExceptions()
                        .stream()
                        .map(ValidationException::getErrorMessage)
                        .collect(Collectors.joining(", ")),
                        exception);
            }
        }

        assertThat(context.getProblems()).satisfiesExactlyInAnyOrder(
                problem -> assertThat(problem).isInstanceOfSatisfying(AttributeIsMissingProblem.class,
                        p -> assertThat(p.getAttributeNameOrPath()).isEqualTo("is_living")),
                problem -> assertThat(problem).isInstanceOfSatisfying(AttributeIsMissingProblem.class,
                        p -> assertThat(p.getAttributeNameOrPath()).isEqualTo("biological_sex")),
                problem -> assertThat(problem).isInstanceOf(JSONSchemaValidationProblem.class)
        );
    }
}
