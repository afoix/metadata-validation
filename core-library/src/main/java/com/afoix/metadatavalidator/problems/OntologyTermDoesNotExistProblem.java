package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class OntologyTermDoesNotExistProblem extends OntologyTermProblem {
    public OntologyTermDoesNotExistProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "The given term does not exist in the ontology.", attributeNameOrPath, attributeValue);
    }
}
