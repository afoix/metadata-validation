package com.afoix.metadatavalidator.entities;

import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;
import org.json.JSONObject;
import org.json.JSONPointerException;

import java.util.stream.Stream;

/**
 * An entity that is a JSON object hierarchy.
 */
public class JSONEntity implements Entity {
    private final JSONObject root;
    private String identifier;

    public JSONEntity(JSONObject root) {
        this.root = root;
    }

    public JSONObject getRoot() {
        return root;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean hasAttribute(String attributeNameOrPath) throws InvalidAttributeNameOrPathException {
        JSONObject context = root;

        if (attributeNameOrPath.contains("/")) {
            int lastSlashIndex = attributeNameOrPath.lastIndexOf('/');
            String parentPath = attributeNameOrPath.substring(0, lastSlashIndex);
            attributeNameOrPath = attributeNameOrPath.substring(lastSlashIndex + 1);
            try {
                context = (JSONObject) root.query(parentPath);
            } catch (JSONPointerException exception) {
                throw new InvalidAttributeNameOrPathException("Attribute path could not be processed as a JSONPointer expression", attributeNameOrPath, exception);
            }
        }

        if (context == null)
            return false;

        return context.has(attributeNameOrPath);
    }

    @Override
    public Stream<Object> getAttributeValues(String attributeNameOrPath) {
        Object result;
        if (attributeNameOrPath.contains("/"))
            result = root.query(attributeNameOrPath);
        else
            result = root.get(attributeNameOrPath);

        if (result == JSONObject.NULL)
            result = null;

        return Stream.of(result);
    }

    @Override
    public void setCaseSensitive(boolean isCaseSensitive) {
        if (!isCaseSensitive) {
            throw new RuntimeException("JSON does not support case-insensitive");
        }
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}
