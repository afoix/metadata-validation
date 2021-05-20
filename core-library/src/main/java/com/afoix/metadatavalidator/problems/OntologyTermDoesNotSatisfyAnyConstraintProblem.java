package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.validators.Validator;

public class OntologyTermDoesNotSatisfyAnyConstraintProblem extends OntologyTermProblem {
    public OntologyTermDoesNotSatisfyAnyConstraintProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue) {
        super(entity, reporter, "The given term does not satisfy any of the constraints on it.", attributeNameOrPath, attributeValue);
    }
}
