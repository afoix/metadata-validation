package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class AttributeValueIsNotOntologyTermRefProblem extends OntologyTermProblem {
    public AttributeValueIsNotOntologyTermRefProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "Value is not an OntologyTermRef.", attributeNameOrPath, attributeValue);
    }
}
