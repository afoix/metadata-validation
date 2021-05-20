package com.afoix.metadatavalidator.ontologies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.ac.ebi.pride.utilities.ols.web.service.model.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class OLSCachingClientTests {
    private OLSClient innerClient;
    private OLSClient client;

    @BeforeEach
    public void setUp() {
        innerClient = Mockito.mock(OLSClient.class);
        client = new OLSCachingClient(innerClient);
    }

    @Test
    public void twoRequests_forTheSameTerm_OnlyCallOLSOnce() {
        Identifier termIdentifier = new Identifier("TEST:123", Identifier.IdentifierType.OBO);

        Term term = Mockito.mock(Term.class);
        when(term.getOboId()).thenReturn(termIdentifier);
        when(term.getIri()).thenReturn(new Identifier("http://test", Identifier.IdentifierType.IRI));
        when(term.getShortName()).thenReturn(new Identifier("TEST_123", Identifier.IdentifierType.OWL));
        when(innerClient.getTermById(termIdentifier, "TEST")).thenReturn(term);

        ITerm returnedTerm = client.getTermById(termIdentifier, "TEST");
        assertThat(returnedTerm).isSameAs(term);

        returnedTerm = client.getTermById(termIdentifier, "TEST");
        assertThat(returnedTerm).isSameAs(term);

        verify(innerClient, times(1)).getTermById(termIdentifier, "TEST");
    }

    @Test
    public void requestingTerm_withDifferentIdentifierToBefore_StillUsesCache() {
        final Identifier oboId = new Identifier("TEST:123", Identifier.IdentifierType.OBO);
        final Identifier iriId = new Identifier("http://test", Identifier.IdentifierType.IRI);
        final Identifier owlId = new Identifier("TEST_123", Identifier.IdentifierType.OWL);

        Term term = Mockito.mock(Term.class);
        when(term.getOboId()).thenReturn(oboId);
        when(term.getIri()).thenReturn(iriId);
        when(term.getShortName()).thenReturn(owlId);
        when(innerClient.getTermById(oboId, "TEST")).thenReturn(term);

        ITerm returnedTerm = client.getTermById(oboId, "TEST");
        assertThat(returnedTerm).isSameAs(term);

        returnedTerm = client.getTermById(iriId, "TEST");
        assertThat(returnedTerm).isSameAs(term);

        returnedTerm = client.getTermById(owlId, "TEST");
        assertThat(returnedTerm).isSameAs(term);

        verify(innerClient, times(1)).getTermById(any(), any());
    }

    private String queryField;

    @Test
    public void twoSearchQueries_withSameParameters_OnlyCallOnce() {
        doAnswer(invocationOnMock -> { queryField = invocationOnMock.getArgument(0); return null; }).when(innerClient).setQueryField(any());
        when(innerClient.getQueryField()).thenAnswer(invocationOnMock -> { return queryField; });

        SearchQuery mockResult = mock(SearchQuery.class);
        when(innerClient.getSearchQuery(anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyInt())).thenReturn(mockResult);
        SearchResponse mockResponse = mock(SearchResponse.class);
        when(mockResult.getResponse()).thenReturn(mockResponse);
        when(mockResponse.getSearchResults()).thenReturn(new SearchResult[0]);

        client.setQueryField("query");
        SearchQuery result1 = client.getSearchQuery(0, "termToSearch", "ontology", true, "childrenOf", false, 1);
        SearchQuery result2 = client.getSearchQuery(0, "termToSearch", "ontology", true, "childrenOf", false, 1);

        assertThat(result2).isSameAs(result1);
        verify(innerClient, times(1)).getSearchQuery(anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyInt());
    }

    @Test
    public void twoSearchQueries_withDifferentParameters_CallTwice() {
        doAnswer(invocationOnMock -> { queryField = invocationOnMock.getArgument(0); return null; }).when(innerClient).setQueryField(any());
        when(innerClient.getQueryField()).thenAnswer(invocationOnMock -> { return queryField; });

        SearchQuery mockResult = mock(SearchQuery.class);
        when(innerClient.getSearchQuery(anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyInt())).thenReturn(mockResult);
        SearchResponse mockResponse = mock(SearchResponse.class);
        when(mockResult.getResponse()).thenReturn(mockResponse);
        when(mockResponse.getSearchResults()).thenReturn(new SearchResult[0]);

        client.setQueryField("query");
        client.getSearchQuery(0, "termToSearch", "ontology", true, "childrenOf", false, 1);
        client.getSearchQuery(0, "differentTerm", "ontology", true, "childrenOf", false, 1);

        verify(innerClient, times(2)).getSearchQuery(anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyInt());
    }

    @Test
    public void twoSearchQueries_withDifferentQueryField_CallTwice() {
        doAnswer(invocationOnMock -> { queryField = invocationOnMock.getArgument(0); return null; }).when(innerClient).setQueryField(any());
        when(innerClient.getQueryField()).thenAnswer(invocationOnMock -> { return queryField; });

        SearchQuery mockResult = mock(SearchQuery.class);
        when(innerClient.getSearchQuery(anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyInt())).thenReturn(mockResult);
        SearchResponse mockResponse = mock(SearchResponse.class);
        when(mockResult.getResponse()).thenReturn(mockResponse);
        when(mockResponse.getSearchResults()).thenReturn(new SearchResult[0]);

        client.setQueryField("query");
        client.getSearchQuery(0, "termToSearch", "ontology", true, "childrenOf", false, 1);
        client.setQueryField("different-query");
        client.getSearchQuery(0, "termToSearch", "ontology", true, "childrenOf", false, 1);

        verify(innerClient, times(2)).getSearchQuery(anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyInt());
    }
}
