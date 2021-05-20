package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class AttributeIsMissingProblem extends BaseAttributeProblem {
    public AttributeIsMissingProblem(Entity entity, Validator reporter, String attributeName) {
        super(entity, reporter, "Attribute is missing.", attributeName);
    }
}
