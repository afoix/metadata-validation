package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;

import java.util.Set;

public class AttributeValuesAreInSetValidator<ValueType> extends AttributeValuesValidator {
    private final Set<ValueType> validValues;

    public AttributeValuesAreInSetValidator(String attributeNameOrPath, Set<ValueType> validValues) {
        super(attributeNameOrPath);
        this.validValues = validValues;
    }

    @Override
    protected void validateValue(Object value, ValidationContext context, AttributeValueProblemBuilder problemBuilder) {
        if (value == null) {
            context.reportProblem(problemBuilder.isNull());
        } else if (!validValues.contains(value)) {
            context.reportProblem(problemBuilder.isNotInPrescribedSet());
        }
    }
}
