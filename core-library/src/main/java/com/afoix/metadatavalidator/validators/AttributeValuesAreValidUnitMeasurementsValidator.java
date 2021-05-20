package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.utils.Measurement;
import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;

import java.util.Set;

public class AttributeValuesAreValidUnitMeasurementsValidator extends AttributeValuesValidator {

    private final Set<String> permittedUnits;

    public AttributeValuesAreValidUnitMeasurementsValidator(String attributeNameOrPath, Set<String> permittedUnits) {
        super(attributeNameOrPath);
        this.permittedUnits = permittedUnits;
    }

    @Override
    protected void validateValue(Object value, ValidationContext context, AttributeValueProblemBuilder problemBuilder) {
        if (value == null)
            return;

        if (value instanceof Measurement measurement) {
            if (!this.permittedUnits.contains(measurement.getUnit())) {
                context.reportProblem(problemBuilder.measurementUnitsNotAcceptable());
            }
            return;
        }

        if (value instanceof Number) {
            context.reportProblem(problemBuilder.measurementMissingUnits());
            return;
        }

        context.reportProblem(problemBuilder.valueIsNotMeasurement());
    }
}
