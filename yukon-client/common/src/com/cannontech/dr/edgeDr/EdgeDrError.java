package com.cannontech.dr.edgeDr;

public class EdgeDrError {
    final int errorCode;
    final String errorMessage;
    
    public EdgeDrError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}
