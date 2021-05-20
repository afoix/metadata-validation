package com.afoix.metadatavalidator.ontologies;

import org.jetbrains.annotations.NotNull;
import uk.ac.ebi.pride.utilities.ols.web.service.model.ITerm;
import uk.ac.ebi.pride.utilities.ols.web.service.model.Identifier;
import uk.ac.ebi.pride.utilities.ols.web.service.model.QueryFields;
import uk.ac.ebi.pride.utilities.ols.web.service.model.SearchQuery;

public interface OLSClient {
    void setQueryField(String queryField);

    SearchQuery getSearchQuery(int page, String termToSearch, String ontology, boolean exactMatch, String childrenOf, boolean obsolete, int size);

    ITerm getTermById(Identifier identifier, @NotNull String ontologyName);

    default boolean isTermChildOf(ITerm term, ITerm ancestor) {
        if (term.getIri().equals(ancestor.getIri()))
            return true;

        setQueryField(new QueryFields.QueryFieldBuilder().setIri().build().toString());

        SearchQuery query = getSearchQuery(0,
                term.getIri().getIdentifier(),
                ancestor.getOntologyName(),
                true,
                ancestor.getIri().getIdentifier(),
                false,
                1);

        return query.getResponse().getNumFound() > 0;
    }

    String getQueryField();
}
