package com.afoix.faang.validation;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.ontologies.OntologySuggestion;
import com.afoix.metadatavalidator.problems.Problem;
import com.afoix.metadatavalidator.validators.ValidationContext;
import com.afoix.metadatavalidator.validators.Validator;

public class ConsolePrintingContext implements ValidationContext {

    boolean hadAnyErrors = false;

    public boolean anyErrors() {
        return hadAnyErrors;
    }

    @Override
    public void reportProblem(Problem problem) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(problem.isError() ? "[ERROR]" : "[WARNING]");
        stringBuilder.append(" ");
        stringBuilder.append(problem.getEntity().getIdentifier());
        stringBuilder.append(" ");
        stringBuilder.append(problem.getMessage());

        System.out.println(stringBuilder);

        if (problem.isError())
            hadAnyErrors = true;
    }

    @Override
    public void reportSuggestedOntologyMapping(Entity entity, Validator reporter, String attributeNameOrPath, OntologySuggestion suggestion) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[SUGGESTION] ");
        stringBuilder.append(entity.getIdentifier());
        stringBuilder.append(": Unbound term \"");
        stringBuilder.append(suggestion.getSourceText());
        stringBuilder.append("\" for attribute \"");
        stringBuilder.append(attributeNameOrPath);
        stringBuilder.append("\" could be mapped to ontology term ");
        stringBuilder.append(suggestion.getSuggestedTerm());

        System.out.println(stringBuilder);
    }
}
