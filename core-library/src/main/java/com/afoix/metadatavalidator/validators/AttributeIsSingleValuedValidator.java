package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;

public class AttributeIsSingleValuedValidator extends AttributeValidator {

    public AttributeIsSingleValuedValidator(String attributeNameOrPath) {
        super(attributeNameOrPath);
    }

    @Override
    public void validate(Entity entity, ValidationContext context) throws MisconfiguredValidatorException {
        try {
            if (entity.getAttributeValues(getAttributeNameOrPath()).count() > 1)
                context.reportProblem(problemBuilder(entity).isMultiValued());
        } catch (InvalidAttributeNameOrPathException e) {
            throw new MisconfiguredValidatorException(e);
        }
    }
}
