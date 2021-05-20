package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.problems.Problem;
import com.afoix.metadatavalidator.ontologies.OntologySuggestion;

import java.util.ArrayList;
import java.util.List;

public class TestValidationContext implements ValidationContext {

    private final List<Problem> problems = new ArrayList<>();
    private final List<OntologySuggestion> suggestions = new ArrayList<>();

    public List<Problem> getProblems() {
        return problems;
    }

    public void clear() {
        problems.clear();
    }

    @Override
    public void reportProblem(Problem problem) {
        problems.add(problem);
    }

    @Override
    public void reportSuggestedOntologyMapping(Entity entity, Validator reporter, String attributeNameOrPath, OntologySuggestion suggestion) {
        suggestions.add(suggestion);
    }

    public List<OntologySuggestion> getSuggestions() {
        return suggestions;
    }
}
