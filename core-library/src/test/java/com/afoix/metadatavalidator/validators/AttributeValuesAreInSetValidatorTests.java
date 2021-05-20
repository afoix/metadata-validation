package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.problems.AttributeValueIsNotInSetProblem;
import com.afoix.metadatavalidator.problems.AttributeValueIsNullProblem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AttributeValuesAreInSetValidator.
 *
 * Incorporates test cases from 'enum_rules()' tests in FAANG codebase:
 * https://github.com/FAANG/dcc-validate-metadata/blob/master/t/validate_attributes.t
 */
@Execution(ExecutionMode.CONCURRENT)
public class AttributeValuesAreInSetValidatorTests {

    public static final String ATTRIBUTE_NAME = "attr";

    private final Validator validator = new AttributeValuesAreInSetValidator<>(ATTRIBUTE_NAME, Set.of("text", "horse"));
    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    @Test
    void value_inSetOfEnumValues_passesValidation() throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, "text");
        validator.validate(entity, context);
        assertThat(context.getProblems()).isEmpty();
    }

    @Test
    public void value_notInSetOfEnumValues_failsValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, 1);
        validator.validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(AttributeValueIsNotInSetProblem.class);
    }

    @Test
    public void nullValue_failsValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, null);
        validator.validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(AttributeValueIsNullProblem.class);
    }
}
