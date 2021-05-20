package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;

public class AttributeIsPresentValidator extends AttributeValidator {

    public AttributeIsPresentValidator(String attributeNameOrPath) {
        super(attributeNameOrPath);
    }

    @Override
    public void validate(Entity entity, ValidationContext context) throws MisconfiguredValidatorException {
        try {
            if (!entity.hasAttribute(getAttributeNameOrPath()))
                context.reportProblem(problemBuilder(entity).isMissing());
        } catch (InvalidAttributeNameOrPathException e) {
            throw new MisconfiguredValidatorException(e);
        }
    }
}
