package com.cannontech.stars.dr.account.service.result;

public class AccountActionResult {
    
    private String accountNumber = "";
    private boolean success = true;
    private String reason = "";
    private int action = -1;
    
    public AccountActionResult() {}
    
    public AccountActionResult(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public AccountActionResult(String accountNumber, boolean success, String reason) {
        this.accountNumber = accountNumber;
        this.success = success;
        this.reason = reason;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setAction(int action) {
        this.action = action;
    }
    
    public int getAction() {
        return action;
    }
    
    @Override
    public String toString() {
        String value = "Account#: " + accountNumber 
        + " Action: " + action 
        + " Success: " + success
        + " Reason: " + reason;
        return value;
    }
}
