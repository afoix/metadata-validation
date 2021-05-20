package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.problems.builders.AttributeProblemBuilder;

public abstract class AttributeValidator extends AbstractValidator {

    private final String attributeNameOrPath;

    public AttributeValidator(String attributeNameOrPath) {
        this.attributeNameOrPath = attributeNameOrPath;
    }

    public String getAttributeNameOrPath() {
        return attributeNameOrPath;
    }

    protected AttributeProblemBuilder problemBuilder(Entity entity) {
        return new AttributeProblemBuilder(entity, this, getAttributeNameOrPath());
    }

    @Override
    public String getDescription() {
        return super.getDescriptionOrDefault(() -> "Validate attribute \"" + attributeNameOrPath + "\"");
    }
}
