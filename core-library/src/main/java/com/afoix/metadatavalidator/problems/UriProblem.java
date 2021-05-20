package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class UriProblem extends BaseAttributeValueProblem {
    public UriProblem(Entity entity, Validator reporter, String message, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, message, attributeNameOrPath, attributeValue);
    }
}
