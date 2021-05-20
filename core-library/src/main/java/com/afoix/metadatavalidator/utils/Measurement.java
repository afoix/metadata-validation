package com.afoix.metadatavalidator.utils;

public class Measurement implements Numeric {

    private final double value;
    private final String unit;

    public Measurement(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Number toNumber() {
        return value;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return getValue() + getUnit();
    }
}
