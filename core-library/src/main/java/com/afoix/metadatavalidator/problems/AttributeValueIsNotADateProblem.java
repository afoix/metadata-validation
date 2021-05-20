package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class AttributeValueIsNotADateProblem extends BaseAttributeValueProblem {
    public AttributeValueIsNotADateProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "Value is not a date or date-like object.", attributeNameOrPath, attributeValue);
    }
}
