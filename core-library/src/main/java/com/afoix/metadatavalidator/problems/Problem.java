package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public interface Problem {
    Entity getEntity();
    Validator getReporter();
    String getMessage();

    boolean isError();
    void setIsError(boolean value);
}
