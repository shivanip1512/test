package com.cannontech.stars.dr.appliance.model;

public class Appliance {
    private ApplianceCategory applianceCategory;
    private int applianceId;
    private int inventoryId;
    private int programId;
    private int accountId;
    private int groupdId;
    private int relay;
    
    public Appliance() {
    }

    public int getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(int applianceId) {
        this.applianceId = applianceId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }
    
    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public int getGroupdId() {
        return groupdId;
    }

    public void setGroupdId(int groupdId) {
        this.groupdId = groupdId;
    }

    public ApplianceCategory getApplianceCategory() {
        return applianceCategory;
    }

    public void setApplianceCategory(ApplianceCategory applianceCategory) {
        this.applianceCategory = applianceCategory;
    }
    
    public int getRelay() {
        return relay;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + accountId;
        result = prime * result + applianceId;
        result = prime * result + groupdId;
        result = prime * result + inventoryId;
        result = prime * result + programId;
        result = prime * result + relay;
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
        final Appliance other = (Appliance) obj;
        if (accountId != other.accountId)
            return false;
        if (applianceId != other.applianceId)
            return false;
        if (groupdId != other.groupdId)
            return false;
        if (inventoryId != other.inventoryId)
            return false;
        if (programId != other.programId)
            return false;
        if (relay != other.relay)
            return false;
        return true;
    }
    
}
