package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.problems.Problem;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.ontologies.OntologySuggestion;

public class DemoteErrorsToWarningsValidator extends AbstractValidator {

    private final Validator innerValidator;

    public DemoteErrorsToWarningsValidator(Validator innerValidator) {
        this.innerValidator = innerValidator;
    }

    @Override
    public void validate(Entity entity, ValidationContext context) throws MisconfiguredValidatorException {
        innerValidator.validate(entity, new ValidationContext() {
            @Override
            public void reportProblem(Problem problem) {
                problem.setIsError(false);
                context.reportProblem(problem);
            }

            @Override
            public void reportSuggestedOntologyMapping(Entity entity, Validator reporter, String attributeNameOrPath, OntologySuggestion suggestion) {
                context.reportSuggestedOntologyMapping(entity, reporter, attributeNameOrPath, suggestion);
            }
        });
    }
}
