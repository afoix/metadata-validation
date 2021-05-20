package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.problems.AttributeValueIsNotNumericProblem;
import com.afoix.metadatavalidator.problems.AttributeValueIsNullProblem;
import com.afoix.metadatavalidator.utils.Measurement;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AttributeValuesAreNumericValidator.
 *
 * Incorporates test cases from 'num_rules()' tests in FAANG codebase:
 * https://github.com/FAANG/dcc-validate-metadata/blob/master/t/validate_attributes.t
 */
@Execution(ExecutionMode.CONCURRENT)
public class AttributeValuesAreNumericValidatorTests {

    public static final String ATTRIBUTE_NAME = "attr";

    private final Validator validator = new AttributeValuesAreNumericValidator(ATTRIBUTE_NAME);
    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    public static Stream<Object> validNumericObjects() {
        return Stream.of(1,
                1.0,
                -1.01,
                "1",
                "1.0",
                "-1.01",
                new Measurement(1.0, "kg"));
    }

    @ParameterizedTest
    @MethodSource("validNumericObjects")
    public void numericValues_passValidation(Object value) throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, value);
        validator.validate(entity, context);
        Assertions.assertThat(context.getProblems()).isEmpty();
    }

    public static Stream<Object> invalidNumericObjects() {
        return Stream.of(
                Arguments.of("text", AttributeValueIsNotNumericProblem.class),
                Arguments.of(null, AttributeValueIsNullProblem.class)
        );
    }

    @ParameterizedTest
    @MethodSource(value="invalidNumericObjects")
    public void nonNumericValues_failValidation(Object value, Class<?> expectedProblemClass) throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, value);
        validator.validate(entity, context);
        Assertions.assertThat(context.getProblems()).singleElement().isInstanceOf(expectedProblemClass);
    }

}
