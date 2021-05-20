package com.afoix.metadatavalidator.services;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.entities.JSONEntity;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

public class BioSamplesService {

    private static final URI API_ENDPOINT = URI.create("https://www.ebi.ac.uk/biosamples/");

    public Entity fetchSample(String sampleId) throws IOException {
        URI uri = API_ENDPOINT.resolve("samples/" + sampleId);
        URLConnection connection = uri.toURL().openConnection();
        connection.setRequestProperty("Accept", "application/hal+json");
        connection.connect();
        try(InputStream stream = connection.getInputStream()) {
            JSONObject root = (JSONObject)new JSONTokener(stream).nextValue();
            return new JSONEntity(root);
        }
    }
}
