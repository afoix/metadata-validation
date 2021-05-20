package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;
import com.afoix.metadatavalidator.problems.builders.ProblemBuilder;
import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;

public abstract class AttributeValuesValidator extends AttributeValidator {

    public AttributeValuesValidator(String attributeNameOrPath) {
        super(attributeNameOrPath);
    }

    @Override
    public void validate(Entity entity, ValidationContext context) throws MisconfiguredValidatorException {
        try {
            for (Object value :
                    (Iterable<Object>)entity.getAttributeValues(getAttributeNameOrPath())::iterator) {
                validateValue(value, context, new ProblemBuilder(entity, this).attribute(getAttributeNameOrPath()).value(value));
            }
        } catch (InvalidAttributeNameOrPathException e) {
            throw new MisconfiguredValidatorException(e);
        }
    }

    protected abstract void validateValue(Object value, ValidationContext context, AttributeValueProblemBuilder problemBuilder) throws MisconfiguredValidatorException;
}
