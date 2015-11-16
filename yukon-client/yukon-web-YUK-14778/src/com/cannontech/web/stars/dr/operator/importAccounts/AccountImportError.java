package com.cannontech.web.stars.dr.operator.importAccounts;

public class AccountImportError {
    private int lineNumber;
    private String errorMessage;
    private String importAccount;
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getImportAccount() {
        return importAccount;
    }
    
    public void setImportAccount(String importAccount) {
        this.importAccount = importAccount;
    }
}
