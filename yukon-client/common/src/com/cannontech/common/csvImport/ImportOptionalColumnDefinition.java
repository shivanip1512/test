package com.cannontech.common.csvImport;

/**
 * Represents an import file column that may be omitted.
 */
public class ImportOptionalColumnDefinition extends ImportColumnDefinition {
    
    public ImportOptionalColumnDefinition(String name, Class<?> typeClass, boolean nullable) {
        super(name, typeClass, nullable, false);
    }
}
