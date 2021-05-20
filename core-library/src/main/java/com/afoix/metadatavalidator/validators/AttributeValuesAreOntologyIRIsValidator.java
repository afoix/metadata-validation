package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;
import com.afoix.metadatavalidator.utils.OntologyTermRef;

public class AttributeValuesAreOntologyIRIsValidator extends AttributeValuesValidator {

    public AttributeValuesAreOntologyIRIsValidator(String attributeNameOrPath) {
        super(attributeNameOrPath);
    }

    @Override
    protected void validateValue(Object value, ValidationContext context, AttributeValueProblemBuilder problemBuilder) throws MisconfiguredValidatorException {
        if (value instanceof OntologyTermRef termRef) {
            if (termRef.getIri() == null) {
                context.reportProblem(problemBuilder.isNotValidOntologyIri());
            }

            return;
        }

        context.reportProblem(problemBuilder.isNotOntologyTermRef());
    }
}
