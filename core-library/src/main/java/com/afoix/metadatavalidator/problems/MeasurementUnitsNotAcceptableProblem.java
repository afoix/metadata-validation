package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class MeasurementUnitsNotAcceptableProblem extends MeasurementProblem {
    public MeasurementUnitsNotAcceptableProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "Value is not using one of the permitted units.", attributeNameOrPath, attributeValue);
    }
}
