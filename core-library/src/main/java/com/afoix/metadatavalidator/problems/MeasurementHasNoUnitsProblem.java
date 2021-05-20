package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class MeasurementHasNoUnitsProblem extends MeasurementProblem {
    public MeasurementHasNoUnitsProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "Measurement has no units.", attributeNameOrPath, attributeValue);
    }
}
