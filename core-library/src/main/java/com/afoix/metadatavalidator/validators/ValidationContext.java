package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.problems.Problem;
import com.afoix.metadatavalidator.ontologies.OntologySuggestion;

public interface ValidationContext {

    void reportProblem(Problem problem);

    void reportSuggestedOntologyMapping(Entity entity,
                                        Validator reporter,
                                        String attributeNameOrPath,
                                        OntologySuggestion suggestion);
}
