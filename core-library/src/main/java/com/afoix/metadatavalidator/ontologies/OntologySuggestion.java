package com.afoix.metadatavalidator.ontologies;

import com.afoix.metadatavalidator.utils.OntologyTermRef;

public class OntologySuggestion {
    private final String sourceText;
    private final OntologyTermRef suggestedTerm;

    public OntologySuggestion(String sourceText, OntologyTermRef suggestedTerm) {
        this.sourceText = sourceText;
        this.suggestedTerm = suggestedTerm;
    }

    public String getSourceText() {
        return sourceText;
    }

    public OntologyTermRef getSuggestedTerm() {
        return suggestedTerm;
    }
}
