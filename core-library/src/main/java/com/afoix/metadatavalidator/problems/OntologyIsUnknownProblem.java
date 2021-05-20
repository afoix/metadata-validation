package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class OntologyIsUnknownProblem extends OntologyTermProblem {
    public OntologyIsUnknownProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "The name of the ontology in which the term is defined could not be determined.", attributeNameOrPath, attributeValue);
    }
}
