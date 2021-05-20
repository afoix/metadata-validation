package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class AttributeValueIsNotInSetProblem extends BaseAttributeValueProblem {
    public AttributeValueIsNotInSetProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "Attribute value is not in the prescribed set.", attributeNameOrPath, attributeValue);
    }
}
