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
        result = prime * result + ((custAtHome == null) ? 0
                : custAtHome.hashCode());
        result = prime * result + ((customerStatus == null) ? 0
                : customerStatus.hashCode());
        result = prime * result + ((propertyNotes == null) ? 0
                : propertyNotes.hashCode());
        result = prime * result + siteInformationId;
        result = prime * result + ((siteNumber == null) ? 0
                : siteNumber.hashCode());
        result = prime * result + streetAddressId;
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
        if (custAtHome == null) {
            if (other.custAtHome != null)
                return false;
        } else if (!custAtHome.equals(other.custAtHome))
            return false;
        if (customerStatus == null) {
            if (other.customerStatus != null)
                return false;
        } else if (!customerStatus.equals(other.customerStatus))
            return false;
        if (propertyNotes == null) {
            if (other.propertyNotes != null)
                return false;
        } else if (!propertyNotes.equals(other.propertyNotes))
            return false;
        if (siteInformationId != other.siteInformationId)
            return false;
        if (siteNumber == null) {
            if (other.siteNumber != null)
                return false;
        } else if (!siteNumber.equals(other.siteNumber))
            return false;
        if (streetAddressId != other.streetAddressId)
            return false;
        return true;
    }

}
