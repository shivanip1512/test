package com.cannontech.stars.dr.hardware.model;

import com.cannontech.common.util.CtiUtilities;

public class StarsStaticLoadGroupMapping {

    private int loadGroupID;
    private int applianceCategoryID = CtiUtilities.NONE_ZERO_ID;
    private String zipCode = "";
    private int consumptionTypeID = CtiUtilities.NONE_ZERO_ID;
    private int switchTypeID = CtiUtilities.NONE_ZERO_ID;

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
