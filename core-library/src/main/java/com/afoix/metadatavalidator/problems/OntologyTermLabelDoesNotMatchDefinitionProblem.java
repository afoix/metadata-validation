package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class OntologyTermLabelDoesNotMatchDefinitionProblem extends OntologyTermProblem {
    private final String expectedLabel;

    public OntologyTermLabelDoesNotMatchDefinitionProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue, String expectedLabel) {
        super(entity, reporter,
                "The label supplied for the ontology term does not match the label declared in the ontology (\""+expectedLabel+"\").",
                attributeNameOrPath, attributeValue);
        this.expectedLabel = expectedLabel;
    }

    public String getExpectedLabel() {
        return expectedLabel;
    }
}
