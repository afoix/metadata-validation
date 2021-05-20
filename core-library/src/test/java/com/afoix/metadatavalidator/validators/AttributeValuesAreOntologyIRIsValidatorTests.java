package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.problems.AttributeValueIsNotOntologyTermRefProblem;
import com.afoix.metadatavalidator.problems.OntologyTermHasNoValidIriProblem;
import com.afoix.metadatavalidator.utils.Measurement;
import com.afoix.metadatavalidator.utils.OntologyTermRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class AttributeValuesAreOntologyIRIsValidatorTests {
    public static final String ATTRIBUTE_NAME = "attr";

    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    @Test
    public void valueIsOntologyTermRefWithIRI_producesNoProblems() throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, OntologyTermRef.ofIri(URI.create("iri:test")));

        new AttributeValuesAreOntologyIRIsValidator(ATTRIBUTE_NAME)
                .validate(entity, context);

        assertThat(context.getProblems()).isEmpty();
    }

    public static Stream<OntologyTermRef> ontologyTermsWithNoIRIs() {
        return Stream.of(
                OntologyTermRef.ofLabel("test"),
                OntologyTermRef.ofOboId("UBERON:1234"),
                OntologyTermRef.ofShortFormId("UBERON_1234")
        );
    }

    @ParameterizedTest
    @MethodSource("ontologyTermsWithNoIRIs")
    public void valueIsOntologyTermRefWithNoIRI_producesProblem(OntologyTermRef value) throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, value);

        new AttributeValuesAreOntologyIRIsValidator(ATTRIBUTE_NAME)
                .validate(entity, context);

        assertThat(context.getProblems()).singleElement().isInstanceOf(OntologyTermHasNoValidIriProblem.class);
    }

    public static Stream<Object> objectsThatAreNotOntologyTerms() {
        return Stream.of(
                1234,
                "test",
                new Measurement(123.4, "m")
        );
    }

    @ParameterizedTest
    @MethodSource("objectsThatAreNotOntologyTerms")
    public void valueIsNotOntologyTermRefWithIRI_producesProblem(Object value) throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, value);

        new AttributeValuesAreOntologyIRIsValidator(ATTRIBUTE_NAME)
                .validate(entity, context);

        assertThat(context.getProblems()).singleElement().isInstanceOf(AttributeValueIsNotOntologyTermRefProblem.class);
    }
}
