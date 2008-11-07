package com.cannontech.stars.dr.account.model;

import org.springframework.core.style.ToStringCreator;

public class ECToAccountMapping {
    
    private int energyCompanyId;
    private int accountId;
    
    public ECToAccountMapping() {}
    
    public int getEnergyCompanyId() {
        return this.energyCompanyId;
    }
    
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public int getAccountId() {
        return this.accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("energyCompanyId", energyCompanyId);
        tsc.append("accountId", accountId);
        return tsc.toString(); 
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + energyCompanyId;
        result = PRIME * result + accountId;
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
        final ECToAccountMapping other = (ECToAccountMapping) obj;
        if (energyCompanyId != other.energyCompanyId)
            return false;
        if (accountId != other.accountId)
            return false;
        return true;
    }
}
