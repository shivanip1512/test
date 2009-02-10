package com.cannontech.stars.dr.account.model;

public class CustomerAccount {

    private int accountId;
    private int accountSiteId;
    private String accountNumber;
    private int customerId;
    private int billingAddressId;
    private String accountNotes;
    
    public CustomerAccount() {
        
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountNotes() {
        return accountNotes;
    }

    public void setAccountNotes(String accountNotes) {
        this.accountNotes = accountNotes;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAccountSiteId() {
        return accountSiteId;
    }

    public void setAccountSiteId(int accountSiteId) {
        this.accountSiteId = accountSiteId;
    }

    public int getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(int billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append(" AccountID " );
        sb.append(this.accountId);
        sb.append(" AccountNumber: ");
        sb.append(this.accountNumber);
        String toString = sb.toString();
        return toString;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + accountId;
        result = PRIME * result + ((accountNotes == null) ? 0 : accountNotes.hashCode());
        result = PRIME * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
        result = PRIME * result + accountSiteId;
        result = PRIME * result + billingAddressId;
        result = PRIME * result + customerId;
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
        final CustomerAccount other = (CustomerAccount) obj;
        if (accountId != other.accountId)
            return false;
        if (accountNumber == null) {
            if (other.accountNumber != null)
                return false;
        } else if (!accountNumber.equals(other.accountNumber))
            return false;
        if (accountSiteId != other.accountSiteId)
            return false;
        if (billingAddressId != other.billingAddressId)
            return false;
        if (customerId != other.customerId)
            return false;
        return true;
    }
    
}
