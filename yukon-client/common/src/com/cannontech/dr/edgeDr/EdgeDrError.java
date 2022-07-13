package com.cannontech.dr.edgeDr;

public class EdgeDrError {
    int errorCode;
    String errorMessage;
    
    public EdgeDrError() {
        super();
    }
    
    public EdgeDrError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    

    public int getErrorCode() {
        return errorCode;
    }


    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }


    public String getErrorMessage() {
        return errorMessage;
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    @Override
    public String toString() {
        return "EdgeDrError [errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
    }
}
