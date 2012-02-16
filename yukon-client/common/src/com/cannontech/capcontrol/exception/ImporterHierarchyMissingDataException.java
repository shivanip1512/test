package com.cannontech.capcontrol.exception;

import com.cannontech.capcontrol.creation.CapControlImporterHierarchyField;

public class ImporterHierarchyMissingDataException extends CapControlImportException {
    private final CapControlImporterHierarchyField missingField;
    
    public ImporterHierarchyMissingDataException(String message, CapControlImporterHierarchyField missingField) {
        super(message);
        this.missingField = missingField;
    }

    public CapControlImporterHierarchyField getMissingField() {
        return missingField;
    }
}
