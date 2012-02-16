package com.cannontech.capcontrol.exception;

import com.cannontech.capcontrol.creation.CapControlImporterCbcField;

public class ImporterCbcMissingDataException extends CapControlImportException {
    private final CapControlImporterCbcField missingField;
    
    public ImporterCbcMissingDataException(String message, CapControlImporterCbcField missingField) {
        super(message);
        this.missingField = missingField;
    }

    public CapControlImporterCbcField getMissingField() {
        return missingField;
    }
}
