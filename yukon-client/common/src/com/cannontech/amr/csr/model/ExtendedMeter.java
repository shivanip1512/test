package com.cannontech.amr.csr.model;

import com.cannontech.amr.meter.model.Meter;

/**
 * Data Transfer Object class for csr pao searches
 */
public class ExtendedMeter extends Meter {

    private String collectionGroup = null;
    private String billingGroup = null;

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

}
