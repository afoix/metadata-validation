package com.afoix.metadatavalidator.services;

import com.afoix.metadatavalidator.utils.OntologyTermRef;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ZoomaServiceTests {

    @Test
    public void query_returnsSuggestedTerms() throws IOException {
        ZoomaService service = new ZoomaService();

        Set<OntologyTermRef> suggestedTerms = new HashSet<>();
        service.query("horse").forEach(suggestedTerms::add);

        assertThat(suggestedTerms).satisfiesExactlyInAnyOrder(
                term -> assertThat(term.getIri()).isEqualTo(URI.create("http://purl.obolibrary.org/obo/NCIT_C14222")),
                term -> assertThat(term.getIri()).isEqualTo(URI.create("http://purl.obolibrary.org/obo/FOODON_03411229"))
        );
    }
}
