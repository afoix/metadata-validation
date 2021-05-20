package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class AttributeValueIsNotAMeasurementProblem extends MeasurementProblem {
    public AttributeValueIsNotAMeasurementProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "Value is not a Measurement.", attributeNameOrPath, attributeValue);
    }
}
