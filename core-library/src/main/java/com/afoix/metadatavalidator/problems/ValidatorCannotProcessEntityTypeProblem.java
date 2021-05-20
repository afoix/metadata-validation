package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class ValidatorCannotProcessEntityTypeProblem extends BaseProblem {
    public ValidatorCannotProcessEntityTypeProblem(Entity entity, Validator reporter, String message) {
        super(entity, reporter, message);
    }
}
