package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class BaseAttributeProblem extends BaseProblem implements AttributeProblem {
    protected final String attributeNameOrPath;

    public BaseAttributeProblem(Entity entity, Validator reporter, String message, String attributeNameOrPath) {
        super(entity, reporter, "Attribute \"" + attributeNameOrPath + "\": " + message);
        this.attributeNameOrPath = attributeNameOrPath;
    }

    public String getAttributeNameOrPath() {
        return attributeNameOrPath;
    }
}
