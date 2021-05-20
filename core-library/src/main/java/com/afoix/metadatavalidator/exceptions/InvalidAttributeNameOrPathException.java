package com.afoix.metadatavalidator.exceptions;

public class InvalidAttributeNameOrPathException extends Exception {

    private final String invalidValue;

    public InvalidAttributeNameOrPathException(String message, String invalidValue, Throwable cause) {
        super(message, cause);
        this.invalidValue = invalidValue;
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}
