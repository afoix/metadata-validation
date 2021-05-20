package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;
import org.xml.sax.SAXParseException;

public class XSDValidationProblem extends BaseProblem {

    private final SAXParseException exception;

    public XSDValidationProblem(Entity entity, Validator reporter, SAXParseException exception, boolean isError) {
        super(entity, reporter, exception.toString());
        this.exception = exception;
        setIsError(isError);
    }

    public SAXParseException getException() {
        return exception;
    }
}
