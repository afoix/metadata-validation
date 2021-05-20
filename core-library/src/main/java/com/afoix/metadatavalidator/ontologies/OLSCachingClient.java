package com.afoix.metadatavalidator.ontologies;

import org.jetbrains.annotations.NotNull;
import uk.ac.ebi.pride.utilities.ols.web.service.model.ITerm;
import uk.ac.ebi.pride.utilities.ols.web.service.model.Identifier;
import uk.ac.ebi.pride.utilities.ols.web.service.model.SearchQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OLSCachingClient implements OLSClient {

    private final OLSClient client;
    private final Map<SearchQueryParameters, SearchQuery> cachedSearchQueries = new HashMap<>();
    private final Map<Identifier, ITerm> cachedTerms = new HashMap<>();
    public OLSCachingClient(OLSClient client) {
        this.client = client;
    }

    @Override
    public void setQueryField(String queryField) {
        client.setQueryField(queryField);
    }
    public String getQueryField() { return client.getQueryField(); }

    @Override
    public SearchQuery getSearchQuery(int page, String termToSearch, String ontology, boolean exactMatch, String childrenOf, boolean obsolete, int size) {
        SearchQueryParameters parameters = new SearchQueryParameters(page, termToSearch, ontology, exactMatch, childrenOf, obsolete, size, client.getQueryField());
        synchronized (cachedSearchQueries) {
            if (cachedSearchQueries.containsKey(parameters)) {
                return cachedSearchQueries.get(parameters);
            }
        }

        SearchQuery result = client.getSearchQuery(page, termToSearch, ontology, exactMatch, childrenOf, obsolete, size);

        synchronized (cachedSearchQueries) {
            cachedSearchQueries.put(parameters, result);
        }

        for (ITerm term :
                result.getResponse().getSearchResults()) {
            cacheTerm(term);
        }

        return result;
    }

    private void cacheTerm(ITerm term) {
        synchronized (cachedTerms) {
            cachedTerms.put(term.getIri(), term);
            cachedTerms.put(term.getOboId(), term);
            cachedTerms.put(term.getShortName(), term);
        }
    }

    @Override
    public ITerm getTermById(Identifier identifier, @NotNull String ontologyName) {
        synchronized (cachedTerms) {
            if (cachedTerms.containsKey(identifier))
                return cachedTerms.get(identifier);
        }

        ITerm term = client.getTermById(identifier, ontologyName);
        cacheTerm(term);
        return term;
    }

    private static class SearchQueryParameters {
        private final int page;
        private final String termToSearch;
        private final String ontology;
        private final boolean exactMatch;
        private final String childrenOf;
        private final boolean obsolete;
        private final int size;
        private final String queryField;

        private SearchQueryParameters(int page, String termToSearch, String ontology, boolean exactMatch, String childrenOf, boolean obsolete, int size, String queryField) {
            this.page = page;
            this.termToSearch = termToSearch;
            this.ontology = ontology;
            this.exactMatch = exactMatch;
            this.childrenOf = childrenOf;
            this.obsolete = obsolete;
            this.size = size;
            this.queryField = queryField;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SearchQueryParameters that = (SearchQueryParameters) o;
            return page == that.page
                    && exactMatch == that.exactMatch
                    && obsolete == that.obsolete
                    && size == that.size
                    && Objects.equals(termToSearch, that.termToSearch)
                    && Objects.equals(ontology, that.ontology)
                    && Objects.equals(childrenOf, that.childrenOf)
                    && Objects.equals(queryField, that.queryField);
        }

        @Override
        public int hashCode() {
            return Objects.hash(page, termToSearch, ontology, exactMatch, childrenOf, obsolete, size, queryField);
        }
    }
}
