package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class AttributeValueIsNotNumericProblem extends BaseAttributeValueProblem {
    public AttributeValueIsNotNumericProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "Value does not appear to be a number.", attributeNameOrPath, attributeValue);
    }
}
