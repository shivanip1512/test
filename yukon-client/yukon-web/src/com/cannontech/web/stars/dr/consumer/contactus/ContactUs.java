package com.cannontech.web.stars.dr.consumer.contactus;

import com.cannontech.database.data.lite.LiteAddress;

public class ContactUs {
    private String companyName;
    private String phoneNumber;
    private String faxNumber;
    private String email;
    private LiteAddress address;
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getFaxNumber() {
        return faxNumber;
    }
    
    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LiteAddress getAddress() {
        return address;
    }
    
    public void setAddress(LiteAddress address) {
        this.address = address;
    }
    
}
