package com.afoix.metadatavalidator.validators;

import java.util.function.Supplier;

public abstract class AbstractValidator implements Validator {

    private String description;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    protected String getDescriptionOrDefault(Supplier<String> defaultDescriptionSupplier) {
        if (description != null) {
            return description;
        }

        return defaultDescriptionSupplier.get();
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
