package com.cannontech.common.csvImport;

import java.util.Collection;

/**
 * Represents the result of validating a single import row. Specifies the type of result and
 * a collection of invalid column names.
 */
public class ImportValidationResult {
    Collection<String> invalidColumns;
    ValidationResultType type;
    
    public ImportValidationResult(Collection<String> invalidColumns, ValidationResultType type) {
        this.invalidColumns = invalidColumns;
        this.type = type;
    }

    public Collection<String> getInvalidColumns() {
        return invalidColumns;
    }

    public ValidationResultType getType() {
        return type;
    }
    
    public boolean isFailed() {
        return type.isFailed();
    }
    
    /**
     * Bad values are generally defined as any non-empty value that is disallowed in a column. For
     * example, a NULL value in a non-nullable column, or a text string in an integer column.
     */
    public boolean isBadValue() {
        return type == ValidationResultType.BAD_TYPE
        || type == ValidationResultType.INVALID_NULL;
    }
    
    /**
     * Missing values are generally defined as any empty value where a real value is required. NULL
     * is considered a non-empty value for this purpose. Disallowed empty values may occur in
     * required columns, grouped columns where other columns in the group contain a value, or
     * value-dependent columns where the depended upon column contains a depended upon value.
     */
    public boolean isMissingValue() {
        return type == ValidationResultType.MISSING_GROUPED
        || type == ValidationResultType.MISSING_REQUIRED
        || type == ValidationResultType.MISSING_VALUE_DEPENDENT;
    }
}
