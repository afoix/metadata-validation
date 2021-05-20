package com.afoix.metadatavalidator.problems;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.utils.OntologyTermRef;
import com.afoix.metadatavalidator.validators.Validator;

public class UseOfRootOntologyTermIsNotPermittedProblem extends OntologyTermProblem {
    private final OntologyTermRef rootTerm;

    public UseOfRootOntologyTermIsNotPermittedProblem(Entity entity, Validator reporter, String attributeNameOrPath, Object attributeValue, OntologyTermRef rootTerm) {
        super(entity, reporter,
                "The value specifies the ontology term \""+rootTerm+"\", but only children of this term should be specified, not the term itself.",
                attributeNameOrPath, attributeValue);
        this.rootTerm = rootTerm;
    }

    public OntologyTermRef getRootTerm() {
        return rootTerm;
    }
}
