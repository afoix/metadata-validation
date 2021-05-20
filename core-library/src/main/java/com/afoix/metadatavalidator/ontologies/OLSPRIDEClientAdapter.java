package com.afoix.metadatavalidator.ontologies;

import org.jetbrains.annotations.NotNull;
import uk.ac.ebi.pride.utilities.ols.web.service.model.ITerm;
import uk.ac.ebi.pride.utilities.ols.web.service.model.Identifier;
import uk.ac.ebi.pride.utilities.ols.web.service.model.SearchQuery;

public class OLSPRIDEClientAdapter implements OLSClient {

    private final uk.ac.ebi.pride.utilities.ols.web.service.client.OLSClient client;

    public OLSPRIDEClientAdapter(uk.ac.ebi.pride.utilities.ols.web.service.client.OLSClient client) {
        this.client = client;
    }

    @Override
    public void setQueryField(String queryField) {
        client.setQueryField(queryField);
    }

    @Override
    public String getQueryField() {
        return client.getQueryField();
    }

    @Override
    public SearchQuery getSearchQuery(int page, String termToSearch, String ontology, boolean exactMatch, String childrenOf, boolean obsolete, int size) {
        return client.getSearchQuery(page, termToSearch, ontology, exactMatch, childrenOf, obsolete, size);
    }

    @Override
    public ITerm getTermById(Identifier identifier, @NotNull String ontologyName) {
        return client.getTermById(identifier, ontologyName);
    }
}
