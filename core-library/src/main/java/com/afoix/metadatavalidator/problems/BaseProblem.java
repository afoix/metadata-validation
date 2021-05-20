package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class BaseProblem implements Problem {

    private final Entity entity;
    private final Validator reporter;

    private String message;
    private boolean isError;

    public BaseProblem(Entity entity, Validator reporter, String message) {
        this.entity = entity;
        this.reporter = reporter;
        this.message = message;
        isError = true;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public Validator getReporter() {
        return reporter;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean value) { isError = value; }

    @Override
    public String toString() {
        return (isError() ? "[error] " : "[warning] ") + getEntity().getIdentifier() + ": "  + getMessage();
    }
}
