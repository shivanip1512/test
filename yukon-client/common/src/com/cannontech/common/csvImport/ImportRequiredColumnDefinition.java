package com.cannontech.common.csvImport;

/**
 * Represents a import file column that must always be present.
 */
public class ImportRequiredColumnDefinition extends ImportColumnDefinition{
    public ImportRequiredColumnDefinition(String name, Class<?> typeClass, boolean nullable, boolean uppercaseValue) {
        super(name, typeClass, true, nullable, uppercaseValue);
    }
}