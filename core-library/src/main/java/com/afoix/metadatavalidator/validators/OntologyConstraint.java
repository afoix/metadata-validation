package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.utils.OntologyTermRef;

public class OntologyConstraint {
    private final OntologyTermRef term;
    private final boolean allowDescendents;
    private final boolean includeRoot;

    public OntologyConstraint(OntologyTermRef term, boolean allowDescendents, boolean includeRoot) {
        this.term = term;
        this.allowDescendents = allowDescendents;
        this.includeRoot = includeRoot;
    }

    public OntologyTermRef getTerm() {
        return term;
    }

    public boolean isAllowDescendents() {
        return allowDescendents;
    }

    public boolean isIncludeRoot() {
        return includeRoot;
    }
}
