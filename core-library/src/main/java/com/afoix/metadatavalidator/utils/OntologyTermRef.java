package com.afoix.metadatavalidator.utils;

import org.jetbrains.annotations.Nullable;
import uk.ac.ebi.pride.utilities.ols.web.service.model.Identifier;

import java.net.URI;
import java.net.URISyntaxException;

public class OntologyTermRef implements Cloneable {
    private String label;
    private String oboId;
    private String shortFormId;
    private String ontologyName;
    private URI iri;

    public String guessOntologyName() {
        if (getOntologyName() != null)
            return getOntologyName();

        if (getOboId() != null) {
            String[] parts = getOboId().split(":");
            if (parts.length == 2)
                return parts[0];
        }

        if (getShortFormId() != null) {
            String[] parts = getShortFormId().split("_");
            if (parts.length == 2)
                return parts[0];
        }

        if (getIri() != null && getIri().toString().contains("/")) {
            String[] parts = getIri().toString().split("/");
            String shortFormId = parts[parts.length - 1];
            if (shortFormId.contains("_")) {
                parts = shortFormId.split("_");
                if (parts.length == 2)
                    return parts[0];
            }
        }

        return null;
    }

    @Nullable
    public Identifier toOLSIdentifier() {

        if (getIri() != null) {
            return new Identifier(getIri().toString(), Identifier.IdentifierType.IRI);
        } else if (getOboId() != null) {
            return new Identifier(getOboId(), Identifier.IdentifierType.OBO);
        } else if (getShortFormId() != null) {
            return new Identifier(getShortFormId(), Identifier.IdentifierType.OWL);
        }

        return null;
    }

    @Override
    public String toString() {
        return "OntologyTermRef{" +
                "label='" + label + '\'' +
                (oboId != null ? (", OBO ID='" + oboId + '\'') : "") +
                (shortFormId != null ? (", ShortForm ID='" + shortFormId + '\'') : "") +
                (ontologyName != null ? (", ontologyName='" + ontologyName + '\'') : "") +
                (iri != null ? (", iri=" + iri) : "") +
                '}';
    }

    public static OntologyTermRef ofLabel(String text) {
        OntologyTermRef term = new OntologyTermRef();
        term.setLabel(text);
        return term;
    }

    public static OntologyTermRef ofOboId(String id) {
        OntologyTermRef term = new OntologyTermRef();
        term.setOboId(id);
        return term;
    }

    public static OntologyTermRef ofOboId(String id, String label) {
        OntologyTermRef term = ofOboId(id);
        term.setLabel(label);
        return term;
    }

    public static OntologyTermRef ofShortFormId(String id) {
        OntologyTermRef term = new OntologyTermRef();
        term.setShortFormId(id);
        return term;
    }

    public static OntologyTermRef ofShortFormId(String id, String label) {
        OntologyTermRef term = ofShortFormId(id);
        term.setLabel(label);
        return term;
    }

    public static OntologyTermRef ofIri(URI iri) {
        OntologyTermRef term = new OntologyTermRef();
        term.setIri(iri);
        return term;
    }

    public static OntologyTermRef ofIri(URI iri, String label) {
        OntologyTermRef term = ofIri(iri);
        term.setLabel(label);
        return term;
    }

    public static OntologyTermRef ofUnknownId(String id) {
        if (id.contains(":") && id.contains("/")) {
            try {
                URI uri = new URI(id);
                return ofIri(uri);
            } catch (URISyntaxException ignored) {
            }
        }

        if (id.contains(":")) {
            return ofOboId(id);
        }

        return ofShortFormId(id);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOboId() {
        return oboId;
    }

    public void setOboId(String oboId) {
        this.oboId = oboId;
    }

    public String getShortFormId() {
        return shortFormId;
    }

    public void setShortFormId(String shortFormId) {
        this.shortFormId = shortFormId;
    }

    public String getOntologyName() {
        return ontologyName;
    }

    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }

    public URI getIri() {
        return iri;
    }

    public void setIri(URI iri) {
        this.iri = iri;
    }

    @Override
    public OntologyTermRef clone() {
        try {
            return (OntologyTermRef)super.clone();
        } catch (CloneNotSupportedException e) {
            OntologyTermRef result = new OntologyTermRef();
            result.iri = iri;
            result.ontologyName = ontologyName;
            result.label = label;
            result.oboId = oboId;
            result.shortFormId = shortFormId;
            return result;
        }
    }
}
