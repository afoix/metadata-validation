package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class RelatedEntityCouldNotBeFoundProblem extends BaseAttributeValueProblem {
    public RelatedEntityCouldNotBeFoundProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "The related entity could not be found.", attributeNameOrPath, attributeValue);
    }
}
