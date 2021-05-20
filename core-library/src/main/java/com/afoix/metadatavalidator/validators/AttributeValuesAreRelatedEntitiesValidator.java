package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;

import java.util.function.Function;

/**
 * A validator that tests that attribute values map to known entities, and optionally validates things
 * about those entities.
 */
public class AttributeValuesAreRelatedEntitiesValidator extends AttributeValuesValidator {

    private final Function<String, Entity> mapRelatedEntity;
    private final Validator relatedEntityValidation;

    public AttributeValuesAreRelatedEntitiesValidator(String attributeNameOrPath, Function<String, Entity> mapRelatedEntity, Validator relatedEntityValidation) {
        super(attributeNameOrPath);
        this.mapRelatedEntity = mapRelatedEntity;
        this.relatedEntityValidation = relatedEntityValidation;
    }

    @Override
    protected void validateValue(Object value, ValidationContext context, AttributeValueProblemBuilder problemBuilder) throws MisconfiguredValidatorException {
        Entity relatedEntity = mapRelatedEntity.apply(value.toString());
        if (relatedEntity == null) {
            context.reportProblem(problemBuilder.relatedEntityNotFound());
            return;
        }

        if (relatedEntityValidation != null) {
            relatedEntityValidation.validate(relatedEntity, context);
        }
    }
}
