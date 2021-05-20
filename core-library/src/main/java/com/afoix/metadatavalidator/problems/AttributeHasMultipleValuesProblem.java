package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class AttributeHasMultipleValuesProblem extends BaseAttributeProblem {

    public AttributeHasMultipleValuesProblem(Entity entity, Validator reporter, String attributeNameOrPath) {
        super(entity, reporter, "Attribute has multiple values.", attributeNameOrPath);
    }
}
