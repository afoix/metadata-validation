package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class AttributeValueIsNullProblem extends BaseAttributeValueProblem {
    public AttributeValueIsNullProblem(Entity entity, Validator reporter, String attributeNameOrPath) {
        super(entity, reporter, "Value is null.", attributeNameOrPath, null);
    }
}
