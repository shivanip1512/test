package com.cannontech.amr.meter.search.model;

import com.cannontech.amr.meter.model.Meter;

/**
 * Data Transfer Object class for meter searches
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
