package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class DateIsNotInAcceptableFormatProblem extends BaseAttributeValueProblem {
    public DateIsNotInAcceptableFormatProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "The date is not in one of the permitted formats.", attributeNameOrPath, attributeValue);
    }
}
