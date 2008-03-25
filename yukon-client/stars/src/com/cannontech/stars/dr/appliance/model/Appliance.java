package com.cannontech.stars.dr.appliance.model;

public class Appliance {
    private ApplianceType applianceType;
    private int applianceId;
    private int inventoryId;
    private int programId;
    private int accountId;
    private int groupdId;
    
    public Appliance() {
    }

    public ApplianceType getApplianceType() {
        return applianceType;
    }

    public void setApplianceType(ApplianceType applianceType) {
        this.applianceType = applianceType;
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

}
