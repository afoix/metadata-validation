package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;
import org.everit.json.schema.ValidationException;

public class JSONSchemaValidationProblem extends BaseProblem {

    private final ValidationException validationException;

    public JSONSchemaValidationProblem(Entity entity, Validator reporter, ValidationException validationException) {
        super(entity, reporter, validationException.getMessage());
        this.validationException = validationException;
    }

    public ValidationException getValidationException() {
        return validationException;
    }
}
