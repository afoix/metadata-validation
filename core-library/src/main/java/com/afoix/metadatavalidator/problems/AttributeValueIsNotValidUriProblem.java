package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class AttributeValueIsNotValidUriProblem extends UriProblem {
    public AttributeValueIsNotValidUriProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "Value is not a valid URI.", attributeNameOrPath, attributeValue);
    }
}
