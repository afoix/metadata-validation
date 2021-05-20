package com.afoix.metadatavalidator.entities;

import com.afoix.metadatavalidator.exceptions.InvalidAttributeNameOrPathException;

import java.util.stream.Stream;

public interface Entity {

    String getIdentifier();

    boolean hasAttribute(String attributeNameOrPath)
            throws InvalidAttributeNameOrPathException;

    Stream<Object> getAttributeValues(String attributeNameOrPath)
            throws InvalidAttributeNameOrPathException;

    void setCaseSensitive(boolean isCaseSensitive);
    boolean isCaseSensitive();
}
