package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public abstract class OntologyTermProblem extends BaseAttributeValueProblem {
    public OntologyTermProblem(Entity entity, Validator reporter, String message, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, message, attributeNameOrPath, attributeValue);
    }
}
