package com.cannontech.capcontrol.exception;

import com.cannontech.capcontrol.creation.model.CbcImportResultType;

public class CapControlCbcImportException extends CapControlImportException {
    private final CbcImportResultType importResultType;
    
    public CapControlCbcImportException(String message, CbcImportResultType importResultType) {
        super(message);
        this.importResultType = importResultType;
    }
    
    public CapControlCbcImportException(String message, CbcImportResultType importResultType, Throwable cause) {
        super(message, cause);
        this.importResultType = importResultType;
    }
    
    public CbcImportResultType getImportResultType() {
        return importResultType;
    }
}
