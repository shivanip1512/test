package com.cannontech.stars.dr.account.model;

public class CustomerAccountWithNames {

    private int accountId;
    private String accountNumber;
    private String lastName;
    private String firstName;
    
    public CustomerAccountWithNames() {
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
        result = PRIME * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
        result = PRIME * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = PRIME * result + ((firstName == null) ? 0 : firstName.hashCode());
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
        final CustomerAccountWithNames other = (CustomerAccountWithNames) obj;
        if (accountId != other.accountId)
            return false;
        if (accountNumber == null) {
            if (other.accountNumber != null)
                return false;
        } else if (!accountNumber.equals(other.accountNumber))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        return true;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
}
