package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;
import com.afoix.metadatavalidator.validators.Validator;

public class BaseAttributeValueProblem extends BaseAttributeProblem implements AttributeValueProblem {

    private final Object attributeValue;

    public BaseAttributeValueProblem(Entity entity, Validator reporter, String message, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, message, attributeNameOrPath);
        this.attributeValue = attributeValue;
    }

    public BaseAttributeValueProblem(AttributeValueProblemBuilder problemBuilder, String message) {
        this(problemBuilder.getAttributeProblemBuilder().getEntity(),
                problemBuilder.getAttributeProblemBuilder().getReporter(),
                message,
                problemBuilder.getAttributeProblemBuilder().getAttributeNameOrPath(),
                problemBuilder.getAttributeValue());
    }

    @Override
    public Object getAttributeValue() {
        return attributeValue;
    }
}
