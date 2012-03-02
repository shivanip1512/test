package com.cannontech.capcontrol.exception;

import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;

public class CapControlHierarchyImportException extends CapControlImportException {
    private final HierarchyImportResultType importResultType;

    public CapControlHierarchyImportException(String message, HierarchyImportResultType importResultType) {
        super(message);
        this.importResultType = importResultType;
    }
    
    public CapControlHierarchyImportException(String message, HierarchyImportResultType importResultType, Throwable cause) {
        super(message, cause);
        this.importResultType = importResultType;
    }

    public HierarchyImportResultType getImportResultType() {
        return importResultType;
    }
}
