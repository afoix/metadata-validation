package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class UriHasNoSchemeProblem extends UriProblem {
    public UriHasNoSchemeProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "Uri has no scheme.", attributeNameOrPath, attributeValue);
    }
}
