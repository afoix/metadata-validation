package com.afoix.metadatavalidator.utils;

public class DateWithFormat {
    private final String dateExpression;
    private final String format;

    public DateWithFormat(String dateExpression, String format) {
        this.dateExpression = dateExpression;
        this.format = format;
    }

    public String getDateExpression() {
        return dateExpression;
    }

    public String getFormat() {
        return format;
    }
}
