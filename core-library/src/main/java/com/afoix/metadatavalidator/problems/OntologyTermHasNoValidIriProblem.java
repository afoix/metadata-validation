package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class OntologyTermHasNoValidIriProblem extends OntologyTermProblem {
    public OntologyTermHasNoValidIriProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "The ontology term has no IRI.", attributeNameOrPath, attributeValue);
    }
}
