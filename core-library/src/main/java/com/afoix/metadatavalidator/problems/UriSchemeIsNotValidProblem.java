package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class UriSchemeIsNotValidProblem extends UriProblem {

    public UriSchemeIsNotValidProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "URI scheme is not on the list of permitted schemes.", attributeNameOrPath, attributeValue);
    }
}
