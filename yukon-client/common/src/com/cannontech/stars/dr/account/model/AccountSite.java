package com.cannontech.stars.dr.account.model;

public class AccountSite {
    private int accountSiteId;
    private int siteInformationId;
    private String siteNumber;
    private int streetAddressId;
    private String propertyNotes;
    private String customerStatus;
    private String custAtHome;
    
    public int getAccountSiteId() {
        return accountSiteId;
    }
    
    public void setAccountSiteId(int accountSiteId) {
        this.accountSiteId = accountSiteId;
    }
    
    public String getSiteNumber() {
        return siteNumber;
    }
    
    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }
    
    public int getStreetAddressId() {
        return streetAddressId;
    }
    
    public void setStreetAddressId(int streetAddressId) {
        this.streetAddressId = streetAddressId;
    }
    
    public String getPropertyNotes() {
        return propertyNotes;
    }
    
    public void setPropertyNotes(String propertyNotes) {
        this.propertyNotes = propertyNotes;
    }
    
    public String getCustomerStatus() {
        return customerStatus;
    }
    
    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }
    
    public String getCustAtHome() {
        return custAtHome;
    }
    
    public void setCustAtHome(String custAtHome) {
        this.custAtHome = custAtHome;
    }

    public void setSiteInformationId(int siteInformationId) {
        this.siteInformationId = siteInformationId;
    }

    public int getSiteInformationId() {
        return siteInformationId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + accountSiteId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AccountSite other = (AccountSite) obj;
        if (accountSiteId != other.accountSiteId)
            return false;
        return true;
    }

}
