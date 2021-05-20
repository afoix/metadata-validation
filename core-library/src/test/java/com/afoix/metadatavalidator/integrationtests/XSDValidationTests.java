package com.afoix.metadatavalidator.integrationtests;

import com.afoix.metadatavalidator.entities.XMLEntity;
import com.afoix.metadatavalidator.loaders.XMLEntityLoader;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.problems.XSDValidationProblem;
import com.afoix.metadatavalidator.validators.ConformsToXSDValidator;
import com.afoix.metadatavalidator.validators.TestValidationContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class XSDValidationTests {

    @NotNull
    private static Stream<Arguments> getMetadataAndSchemaPairs(String validity) throws URISyntaxException, IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Path rootDir = Path.of(Objects.requireNonNull(loader.getResource("testcases/xsd")).toURI());
        Path schemasRoot = rootDir.resolve("schemas");
        Path metadataRoot = rootDir.resolve("metadata");

        Map<String, File> xsdFiles = new HashMap<>();
        Files.walk(schemasRoot)
                .filter(path -> path.toString().toLowerCase().endsWith(".xsd"))
                .forEach(xsdFile -> xsdFiles.put(FilenameUtils.getBaseName(xsdFile.toString())
                        .replace('.', '-')
                        .toLowerCase(), xsdFile.toFile()));

        return Files.walk(metadataRoot)
                .filter(path -> path.toString().toLowerCase().endsWith(".xml"))
                .filter(path -> metadataRoot.relativize(path).subpath(1, 2).toString().equals(validity))
                .sorted()
                .map(xmlFile -> {
                    // Figure out which XSD file goes with this XML file
                    String groupName = metadataRoot.relativize(xmlFile).subpath(0, 1).toString();
                    File xsdFile = xsdFiles.get(groupName);
                    assert xsdFile != null;
                    return Arguments.of(
                            Named.of(rootDir.relativize(xsdFile.toPath()).toString(), xsdFile),
                            Named.of(rootDir.relativize(xmlFile).toString(), xmlFile.toFile())
                    );
                });
    }

    private static Stream<Arguments> validXSDMetadataSamples() throws URISyntaxException, IOException {
        return getMetadataAndSchemaPairs("valid");
    }

    @ParameterizedTest
    @MethodSource("validXSDMetadataSamples")
    public void validMetadataWithXSD(File schemaFile, File xmlFile)
            throws SAXException,
                   IOException,
                   InvalidFormatException,
                   MisconfiguredValidatorException {

        List<XMLEntity> entities = new XMLEntityLoader(xmlFile).load().toList();

        ConformsToXSDValidator validator = new ConformsToXSDValidator(schemaFile);

        TestValidationContext context = new TestValidationContext();
        for (XMLEntity entity : entities) {
            validator.validate(entity, context);
        }

        assertThat(context.getProblems()).isEmpty();
    }

    private static Stream<Arguments> invalidXSDMetadataSamples() throws URISyntaxException, IOException {
        return getMetadataAndSchemaPairs("invalid");
    }

    @ParameterizedTest
    @MethodSource("invalidXSDMetadataSamples")
    public void invalidMetadataWithXSD(File schemaFile, File xmlFile)
            throws SAXException,
            IOException,
            InvalidFormatException,
            MisconfiguredValidatorException {

        List<XMLEntity> entities = new XMLEntityLoader(xmlFile).load().toList();

        ConformsToXSDValidator validator = new ConformsToXSDValidator(schemaFile);

        TestValidationContext context = new TestValidationContext();
        for (XMLEntity entity : entities) {
            validator.validate(entity, context);
        }

        assertThat(context.getProblems()).hasAtLeastOneElementOfType(XSDValidationProblem.class);
    }
}
