package com.cannontech.common.model;

import java.util.List;

import com.cannontech.common.util.LazyList;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;

public class ServiceCompanyDto {
    //Service Company
    private int companyId;
    private String companyName;
    private String mainPhoneNumber;
    private String mainFaxNumber;
    private String hiType;
    private int energyCompanyId;
    
    /* RELATIONS */
    //address
    private LiteAddress address;
    
    //primary contact
    private LiteContact primaryContact;
    
    //contact notification
    private String emailContactNotification;
    
    //designation codes
    private List<DesignationCodeDto> designationCodes = LazyList.ofInstance(DesignationCodeDto.class);
    
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
    
    public String getHiType() {
        return hiType;
    }
    
    public void setHiType(String hiType) {
        this.hiType = hiType;
    }

    public void setPrimaryContact(LiteContact primaryContact) {
        this.primaryContact = primaryContact;
    }

    public LiteContact getPrimaryContact() {
        return primaryContact;
    }

    public void setAddress(LiteAddress address) {
        this.address = address;
    }

    public LiteAddress getAddress() {
        return address;
    }

    public void setDesignationCodes(List<DesignationCodeDto> designationCodes) {
        this.designationCodes = designationCodes;
    }

    public List<DesignationCodeDto> getDesignationCodes() {
        return designationCodes;
    }

    public void setEmailContactNotification(String contactNotification) {
        this.emailContactNotification = contactNotification;
    }

    public String getEmailContactNotification() {
        return emailContactNotification;
    }
    
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    public int getEnergyCompanyId() {
        return energyCompanyId;
    }
}