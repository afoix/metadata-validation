package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;

/**
 * Interface for objects that can do some kind of validation check to an Entity.
 */
public interface Validator {
    void validate(Entity entity, ValidationContext context)
            throws MisconfiguredValidatorException;

    String getDescription();
    void setDescription(String description);
}
