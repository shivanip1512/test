package com.cannontech.stars.dr.hardware.model;


public class StarsStaticLoadGroupMapping {

    private String loadGroupName;
    private int loadGroupID;
    private int applianceCategoryID;
    private String zipCode = "";
    private int consumptionTypeID;
    private int switchTypeID;
    
    public String getLoadGroupName() {
        return loadGroupName;
    }
    
    public void setLoadGroupName(String loadGroupName) {
        this.loadGroupName = loadGroupName;
    }
    
    public int getLoadGroupID() {
        return loadGroupID;
    }

    public void setLoadGroupID(int loadGroupID) {
        this.loadGroupID = loadGroupID;
    }

    public int getApplianceCategoryID() {
        return applianceCategoryID;
    }

    public void setApplianceCategoryID(int applianceCategoryID) {
        this.applianceCategoryID = applianceCategoryID;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getConsumptionTypeID() {
        return consumptionTypeID;
    }

    public void setConsumptionTypeID(int consumptionTypeID) {
        this.consumptionTypeID = consumptionTypeID;
    }

    public int getSwitchTypeID() {
        return switchTypeID;
    }

    public void setSwitchTypeID(int switchTypeID) {
        this.switchTypeID = switchTypeID;
    }
}
