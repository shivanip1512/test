package com.cannontech.common.csvImport;

/**
 * Base class for all import column definitions. The shared characteristics of all columns are:
 * 
 * Name: A case-insensitive string identifier.
 * Type Class: The type of values permitted in this column. The class passed in this parameter should
 *             have a static valueOf(String) method for validation purposes.
 * Nullable: Whether or not "NULL" values are permitted in this column. Values with the exact
 *           string "NULL" will be transformed to "" (empty string) when parsed into ImportData.
 * Required: Whether the column is required or optional.
 * 
 * New column definition types should extend this class. New column definitions will also need to
 * have validation added to ImportFileValidator.
 */
public abstract class ImportColumnDefinition {
    private final String name;
    private final Class<?> typeClass;
    private boolean nullable;
    private boolean required;
    
    protected ImportColumnDefinition(String name, Class<?> typeClass, boolean nullable, boolean required) {
        this.name = name.toUpperCase();
        this.typeClass = typeClass;
        this.nullable = nullable;
        this.required = required;
    }
    
    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return typeClass;
    }

    public boolean isNullable() {
        return nullable;
    }
    
    public boolean isRequired() {
        return required;
    }
}
