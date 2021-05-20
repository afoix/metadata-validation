package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;
import com.afoix.metadatavalidator.utils.Numeric;
import org.apache.commons.lang3.math.NumberUtils;

public class AttributeValuesAreNumericValidator
        extends AttributeValuesValidator {

    public AttributeValuesAreNumericValidator(String attributeNameOrPath) {
        super(attributeNameOrPath);
    }

    @Override
    protected void validateValue(Object value,
                                 ValidationContext context,
                                 AttributeValueProblemBuilder problemBuilder) {
        if (value == null) {
            context.reportProblem(problemBuilder.isNull());
            return;
        }

        if (value instanceof Number || value instanceof Numeric)
            return;

        String stringValue = value.toString();
        if (!NumberUtils.isCreatable(stringValue))
            context.reportProblem(problemBuilder.isNotNumeric());
    }
}
