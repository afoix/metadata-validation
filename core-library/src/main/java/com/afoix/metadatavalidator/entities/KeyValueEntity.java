package com.afoix.metadatavalidator.entities;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * An entity that is a flat collection of key/value pairs, and is not hierarchical. There may be multiple values for
 * any given key.
 */
public class KeyValueEntity implements Entity {
    private final List<Map.Entry<String, Object>> data = new ArrayList<>();
    private String identifier;
    private boolean caseSensitive;

    public KeyValueEntity() {
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void putAdditionally(String key, Object value) {
        data.add(new AbstractMap.SimpleEntry<>(key, value));
    }

    public void putReplacing(String key, Object value) {
        data.removeIf(entry -> entry.getKey().equals(key));
        putAdditionally(key, value);
    }

    @Override
    public boolean hasAttribute(String attributeNameOrPath) {
        return data.stream().map(Map.Entry::getKey)
                .anyMatch(key -> this.caseSensitive
                        ? key.equals(attributeNameOrPath)
                        : key.equalsIgnoreCase(attributeNameOrPath));
    }

    @Override
    public Stream<Object> getAttributeValues(String attributeNameOrPath) {
        return data.stream().filter(entry -> this.caseSensitive
                ? entry.getKey().equals(attributeNameOrPath)
                : entry.getKey().equalsIgnoreCase(attributeNameOrPath))
                .map(Map.Entry::getValue);
    }

    @Override
    public void setCaseSensitive(boolean isCaseSensitive) {
        this.caseSensitive = isCaseSensitive;
    }

    @Override
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void clear() {
        data.clear();
    }
}
