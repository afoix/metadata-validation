package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;

public abstract class ConditionalValidator extends AbstractValidator {

    private final Validator innerValidator;

    public ConditionalValidator(Validator innerValidator) {
        this.innerValidator = innerValidator;
    }

    @Override
    public String getDescription() {
        return getDescriptionOrDefault(() -> innerValidator.getDescription() + "(conditionally)");
    }

    @Override
    public void validate(Entity entity, ValidationContext context) throws MisconfiguredValidatorException {
        if (shouldValidate(entity)) {
            innerValidator.validate(entity, context);
        }
    }

    protected abstract boolean shouldValidate(Entity entity);
}
