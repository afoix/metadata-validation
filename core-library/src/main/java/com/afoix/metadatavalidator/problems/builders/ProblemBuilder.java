package com.afoix.metadatavalidator.problems.builders;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class ProblemBuilder {
    private final Entity entity;
    private final Validator validator;

    public ProblemBuilder(Entity entity, Validator validator) {
        this.entity = entity;
        this.validator = validator;
    }

    public AttributeProblemBuilder attribute(String attributeNameOrPath) {
        return new AttributeProblemBuilder(entity, validator, attributeNameOrPath);
    }
}
