package com.cannontech.amr.csr.model;

/**
 * Data Transfer Object class for csr pao searches
 */
public class SearchPao {

    private int paoId = 0;
    private String meterNumber = null;
    private String paoName = null;
    private String type = null;
    private String address = null;
    private String collectionGroup = null;
    private String billingGroup = null;
    private String route = null;

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBillingGroup() {
        return billingGroup;
    }

    public void setBillingGroup(String billingGroup) {
        this.billingGroup = billingGroup;
    }

    public String getCollectionGroup() {
        return collectionGroup;
    }

    public void setCollectionGroup(String collectionGroup) {
        this.collectionGroup = collectionGroup;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getPaoName() {
        return paoName;
    }

    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
