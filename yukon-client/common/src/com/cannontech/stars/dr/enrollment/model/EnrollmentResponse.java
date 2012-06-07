package com.cannontech.stars.dr.enrollment.model;

public class EnrollmentResponse{
    String accountNumber;
    String serialNumber;
    String loadProgramName;
    String loadGroupName;
    boolean success;
    String failureErrorCode;
    String failureErrorReference;
    String failureErrorDescription;
    
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLoadProgramName() {
        return loadProgramName;
    }
    public void setLoadProgramName(String loadProgramName) {
        this.loadProgramName = loadProgramName;
    }

    public String getLoadGroupName() {
        return loadGroupName;
    }
    public void setLoadGroupName(String loadGroupName) {
        this.loadGroupName = loadGroupName;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFailureErrorCode() {
        return failureErrorCode;
    }
    public void setFailureErrorCode(String failureErrorCode) {
        this.failureErrorCode = failureErrorCode;
    }

    public String getFailureErrorReference() {
        return failureErrorReference;
    }
    public void setFailureErrorReference(String failureErrorReference) {
        this.failureErrorReference = failureErrorReference;
    }

    public String getFailureErrorDescription() {
        return failureErrorDescription;
    }
    public void setFailureErrorDescription(String failureErrorDescription) {
        this.failureErrorDescription = failureErrorDescription;
    }
    
}