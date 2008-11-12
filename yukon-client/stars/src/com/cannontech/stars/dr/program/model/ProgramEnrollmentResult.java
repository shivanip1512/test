package com.cannontech.stars.dr.program.model;


public class ProgramEnrollmentResult {
    
    String accountNumber;
    long serialNumber;
    String loadProgramName;
    String loadGroupName;

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public long getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(long serialNumber) {
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

}
