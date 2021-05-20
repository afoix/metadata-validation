package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.ontologies.OLSClient;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import com.afoix.metadatavalidator.problems.builders.AttributeValueProblemBuilder;
import com.afoix.metadatavalidator.utils.OntologyTermRef;

public class AttributeValuesAreNCBITaxonIDsValidator extends AttributeValuesAreOntologyTermsValidator {
    public AttributeValuesAreNCBITaxonIDsValidator(String attributeNameOrPath, OLSClient olsClient) throws MisconfiguredValidatorException {
        super(attributeNameOrPath, olsClient, true, null);
    }

    @Override
    protected void validateValue(Object value, ValidationContext context, AttributeValueProblemBuilder problemBuilder) throws MisconfiguredValidatorException {
        OntologyTermRef termRef;
        if (value instanceof OntologyTermRef t) {
            termRef = t.clone();
            // Force this to be an NCBI term
            termRef.setOntologyName("NCBITaxon");
        } else {
            termRef = OntologyTermRef.ofShortFormId("NCBITaxon_" + value.toString());
        }
        super.validateValue(termRef, context, problemBuilder);
    }
}
