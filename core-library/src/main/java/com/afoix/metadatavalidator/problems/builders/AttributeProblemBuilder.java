package com.afoix.metadatavalidator.problems.builders;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.problems.AttributeHasMultipleValuesProblem;
import com.afoix.metadatavalidator.problems.AttributeIsMissingProblem;
import com.afoix.metadatavalidator.problems.AttributeProblem;
import com.afoix.metadatavalidator.validators.Validator;

public class AttributeProblemBuilder {
    final Entity entity;
    final Validator reporter;
    final String attributeNameOrPath;

    public AttributeProblemBuilder(Entity entity, Validator reporter, String attributeNameOrPath) {
        this.entity = entity;
        this.reporter = reporter;
        this.attributeNameOrPath = attributeNameOrPath;
    }

    public AttributeProblem isMissing() {
        return new AttributeIsMissingProblem(entity, reporter, attributeNameOrPath);
    }

    public AttributeProblem isMultiValued() {
        return new AttributeHasMultipleValuesProblem(entity, reporter, attributeNameOrPath);
    }

    public AttributeValueProblemBuilder value(Object value) {
        return new AttributeValueProblemBuilder(this, value);
    }

    public Entity getEntity() {
        return entity;
    }

    public Validator getReporter() {
        return reporter;
    }

    public String getAttributeNameOrPath() {
        return attributeNameOrPath;
    }
}
