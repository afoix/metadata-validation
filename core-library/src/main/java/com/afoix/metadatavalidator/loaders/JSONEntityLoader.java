package com.afoix.metadatavalidator.loaders;

import com.afoix.metadatavalidator.entities.JSONEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class JSONEntityLoader implements EntityLoader<JSONEntity> {
    private final InputStream inputStream;
    private String sourceName;
    private boolean ownsStream;

    public JSONEntityLoader(File sourceFile) throws FileNotFoundException {
        this(new FileInputStream(sourceFile));
        sourceName = sourceFile.getName();
        ownsStream = true;
    }

    public JSONEntityLoader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public JSONEntityLoader withSourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    @Override
    public Stream<JSONEntity> load() throws IOException {
        Object rootObj = new JSONTokener(inputStream).nextValue();
        if (ownsStream) {
            inputStream.close();
        }

        if (rootObj instanceof JSONObject) {
            JSONEntity jsonEntity = new JSONEntity((JSONObject) rootObj);
            jsonEntity.setIdentifier(sourceName);
            return Stream.of(jsonEntity);
        }

        if (rootObj instanceof JSONArray array) {
            List<JSONEntity> results = new ArrayList<>();
            int position = 0;
            for (Iterator<Object> iterator = array.iterator(); iterator.hasNext(); ) {
                Object object = iterator.next();
                JSONObject arrayEntry = (JSONObject) object;
                JSONEntity entity = new JSONEntity(arrayEntry);
                entity.setIdentifier(sourceName + "#" + position);
                results.add(entity);
                position++;
            }
            return results.stream();
        }

        throw new JSONException("Invalid type of json token at root");
    }
}
