package com.cannontech.stars.dr.enrollment.model;


public class EnrollmentHelper {
    
    String accountNumber;
    String programName;
    String serialNumber;
    String applianceCategoryName;
    String loadGroupName;
    Float applianceKW;
    boolean seasonalLoad;
    String relay;
    
    public EnrollmentHelper(){}
    public EnrollmentHelper(String accountNumber, String loadGroupName,
            String programName, String serialNumber) {
        super();
        this.accountNumber = accountNumber;
        this.loadGroupName = loadGroupName;
        this.programName = programName;
        this.serialNumber = serialNumber;
    }
    public EnrollmentHelper(String accountNumber, String applianceCategoryName,
            Float applianceKW, String loadGroupName, String programName,
            String relay, boolean seasonalLoad, String serialNumber) {
        super();
        this.accountNumber = accountNumber;
        this.applianceCategoryName = applianceCategoryName;
        this.applianceKW = applianceKW;
        this.loadGroupName = loadGroupName;
        this.programName = programName;
        this.relay = relay;
        this.seasonalLoad = seasonalLoad;
        this.serialNumber = serialNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getProgramName() {
        return programName;
    }
    public void setProgramName(String programName) {
        this.programName = programName;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getApplianceCategoryName() {
        return applianceCategoryName;
    }
    public void setApplianceCategoryName(String applianceCategoryName) {
        this.applianceCategoryName = applianceCategoryName;
    }
    
    public String getLoadGroupName() {
        return loadGroupName;
    }
    public void setLoadGroupName(String loadGroupName) {
        this.loadGroupName = loadGroupName;
    }
    
    public Float getApplianceKW() {
        return applianceKW;
    }
    public void setApplianceKW(Float applianceKW) {
        this.applianceKW = applianceKW;
    }

    public String getRelay() {
        return relay;
    }
    public void setRelay(String relay) {
        this.relay = relay;
    }
    
    public boolean isSeasonalLoad() {
        return seasonalLoad;
    }
    public void setSeasonalLoad(boolean seasonalLoad) {
        this.seasonalLoad = seasonalLoad;
    }
}