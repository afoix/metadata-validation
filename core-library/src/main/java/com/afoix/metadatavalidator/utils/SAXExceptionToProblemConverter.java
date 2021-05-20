package com.afoix.metadatavalidator.utils;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.problems.XSDValidationProblem;
import com.afoix.metadatavalidator.validators.ValidationContext;
import com.afoix.metadatavalidator.validators.Validator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class SAXExceptionToProblemConverter implements ErrorHandler {
    private final ValidationContext context;
    private final Entity entity;
    private final Validator reporter;

    public SAXExceptionToProblemConverter(ValidationContext context, Entity entity, Validator reporter) {
        this.context = context;
        this.entity = entity;
        this.reporter = reporter;
    }

    @Override
    public void warning(SAXParseException exception) {
        context.reportProblem(new XSDValidationProblem(entity, reporter, exception, false));
    }

    @Override
    public void error(SAXParseException exception) {
        context.reportProblem(new XSDValidationProblem(entity, reporter, exception, true));
    }

    @Override
    public void fatalError(SAXParseException exception) {
        context.reportProblem(new XSDValidationProblem(entity, reporter, exception, true));
    }
}
