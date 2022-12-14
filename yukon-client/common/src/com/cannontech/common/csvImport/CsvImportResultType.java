package com.cannontech.common.csvImport;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Represents the set of possible results an import attempt can have. While there are a variety of
 * possible precise results, they are all categorized broadly as either successful or failed.
 */
public enum CsvImportResultType implements DisplayableEnum{
    SUCCESS(true),
    INVALID_IMPORT_ACTION(false),
    OBJECT_EXISTS(false),
    NO_SUCH_OBJECT(false),
    MISSING_DATA(false),
    BAD_DATA(false),
    CONDITIONAL_BAD_DATA(false),
    HAS_DEPENDENCIES(false),
    INVALID_CHARS(false),
    INVALID_CONFIG_ADD(false),
    INVALID_CONFIG_UPDATE(false),
    ;
    
    private boolean success;
    
    private CsvImportResultType(boolean success) {
        this.success = success;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getFormatKey() {
        return "yukon.exception.processingException." + this.name();
    }
    
}
