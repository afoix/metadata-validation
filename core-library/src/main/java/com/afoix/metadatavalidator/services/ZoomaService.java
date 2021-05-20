package com.afoix.metadatavalidator.services;

import com.afoix.metadatavalidator.utils.OntologyTermRef;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ZoomaService {

    private static final URI API_ENDPOINT = URI.create("https://www.ebi.ac.uk/spot/zooma/v2/api/");

    public Stream<OntologyTermRef> query(String text) throws IOException {
        URI uri = UriComponentsBuilder.fromUri(API_ENDPOINT)
                .path("services/annotate")
                .queryParam("propertyValue", text)
                .build().toUri();

        URLConnection connection = uri.toURL().openConnection();
        connection.setRequestProperty("Accept", "application/json");
        connection.connect();
        try(InputStream stream = connection.getInputStream()) {
            List<Object> root = ((JSONArray)new JSONTokener(stream).nextValue()).toList();

            // Sort so that highest confidence is first
            root.sort((aObj, bObj) -> {
                Map<String, Object> a = (Map<String, Object>) aObj;
                Map<String, Object> b = (Map<String, Object>) bObj;
                String aConfidence = (String)a.get("confidence");
                String bConfidence = (String)b.get("confidence");

                if (aConfidence.equals(bConfidence)) return 0;
                if (aConfidence.equals("HIGH")) return -1;
                if (aConfidence.equals("LOW")) return 1;
                return 0;
            });

            return root.stream().flatMap(obj -> {
                Map<String, Object> map = (Map<String, Object>) obj;
                List<String> semanticTags = (List<String>) map.get("semanticTags");
                return semanticTags.stream()
                        .map(iri -> OntologyTermRef.ofIri(URI.create(iri)));
            });
        }
    }

}
