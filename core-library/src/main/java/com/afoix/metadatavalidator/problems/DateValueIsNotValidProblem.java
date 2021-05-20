package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class DateValueIsNotValidProblem extends BaseAttributeValueProblem {
    public DateValueIsNotValidProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "Date is not valid.", attributeNameOrPath, attributeValue);
    }
}
