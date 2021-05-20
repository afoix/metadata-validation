package com.afoix.metadatavalidator.utils;

/**
 * Interface for objects that represent numeric values, without actually being numbers. For example, a class that
 * is a number with an associated unit of measurement.
 */
public interface Numeric {
    Number toNumber();
}
