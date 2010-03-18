package com.cannontech.common.model;

public class ServiceCompanyDto {
    private int companyId;
    private String companyName;
    private int addressId;
    private String mainPhoneNumber;
    private String mainFaxNumber;
    private int primaryContactId;
    private String hiType;
    
    public int getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public int getAddressId() {
        return addressId;
    }
    
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
    
    public String getMainPhoneNumber() {
        return mainPhoneNumber;
    }
    
    public void setMainPhoneNumber(String mainPhoneNumber) {
        this.mainPhoneNumber = mainPhoneNumber;
    }
    
    public String getMainFaxNumber() {
        return mainFaxNumber;
    }
    
    public void setMainFaxNumber(String mainFaxNumber) {
        this.mainFaxNumber = mainFaxNumber;
    }
    
    public int getPrimaryContactId() {
        return primaryContactId;
    }
    
    public void setPrimaryContactId(int primaryContactId) {
        this.primaryContactId = primaryContactId;
    }
    
    public String getHiType() {
        return hiType;
    }
    
    public void setHiType(String hiType) {
        this.hiType = hiType;
    }
}