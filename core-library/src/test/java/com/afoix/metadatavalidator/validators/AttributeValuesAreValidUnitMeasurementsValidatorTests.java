package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.KeyValueEntity;
import com.afoix.metadatavalidator.problems.AttributeValueIsNotAMeasurementProblem;
import com.afoix.metadatavalidator.problems.MeasurementHasNoUnitsProblem;
import com.afoix.metadatavalidator.problems.MeasurementUnitsNotAcceptableProblem;
import com.afoix.metadatavalidator.utils.Measurement;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AttributeValuesAreValidUnitMeasurementsValidator.
 *
 * Incorporates test cases from 'unit_rules()' tests in FAANG codebase:
 * https://github.com/FAANG/dcc-validate-metadata/blob/master/t/validate_attributes.t
 */
@Execution(ExecutionMode.CONCURRENT)
public class AttributeValuesAreValidUnitMeasurementsValidatorTests {

    public static final String ATTRIBUTE_NAME = "attr";

    private final Validator validator = new AttributeValuesAreValidUnitMeasurementsValidator(ATTRIBUTE_NAME, Set.of("kg"));
    private final KeyValueEntity entity = new KeyValueEntity();
    private final TestValidationContext context = new TestValidationContext();

    @BeforeEach
    public void reset() {
        entity.clear();
        context.clear();
    }

    @Test
    void measurementWithPermittedUnits_passesValidation() throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, new Measurement(10, "kg"));
        validator.validate(entity, context);
        assertThat(context.getProblems()).isEmpty();
    }

    @Test
    void measurementWithNotPermittedUnits_failsValidation() throws MisconfiguredValidatorException {
        entity.putAdditionally(ATTRIBUTE_NAME, new Measurement(10, "picoseconds"));
        validator.validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(MeasurementUnitsNotAcceptableProblem.class);
    }

    @Test
    public void numericWithNoUnits_failsValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, 10);
        validator.validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(MeasurementHasNoUnitsProblem.class);
    }

    @Test
    public void nonNumeric_failsValidation() throws MisconfiguredValidatorException {
        entity.putReplacing(ATTRIBUTE_NAME, "text");
        validator.validate(entity, context);
        assertThat(context.getProblems()).singleElement().isInstanceOf(AttributeValueIsNotAMeasurementProblem.class);
    }

}
