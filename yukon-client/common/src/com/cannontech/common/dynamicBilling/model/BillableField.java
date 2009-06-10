package com.cannontech.common.dynamicBilling.model;

/**
 * Enumeration of all of the possible field types a BillableDevice may have
 */
public enum BillableField {
    meterNumber(true, false, false, false, null), 
    meterPositionNumber(true, false, false, false, null), 
    accountNumber(true, false, false, false, null), 
    paoName(true, false, false, false, null), 
    address(true, false, false, false, null), 
    totalConsumption(true, true, true, true, "T"), 
    totalPeakDemand(true, true, true, true, "T"), 
    rateAConsumption(true, true, true, true, "A"), 
    rateBConsumption(true, true, true, true, "B"), 
    rateCConsumption(true, true, true, true, "C"), 
    rateDConsumption(true, true, true, true, "D"), 
    rateADemand(true, true, true, true, "A"), 
    rateBDemand(true, true, true, true, "B"), 
    rateCDemand(true, true, true, true, "C"), 
    rateDDemand(true, true, true, true, "D");

    private boolean hasData = false;
    private boolean hasValue = false;
    private boolean hasTimestamp = false;
    private boolean hasUnitOfMeasure = false;
    private String rate = null;

    BillableField(boolean hasData, boolean hasValue, boolean hasTimestamp, 
                  boolean hasUnitOfMeasure, String rate) {
        this.hasData = hasData;
        this.hasValue = hasValue;
        this.hasTimestamp = hasTimestamp;
        this.hasUnitOfMeasure = hasUnitOfMeasure;
        this.rate = rate;
    }

    /**
     * Method to check to see if this BillableField has a data value
     * @return True if has a data value
     */
    public boolean hasData() {
        return this.hasData;
    }

    /**
     * Method to check to see if this BillableField has a value
     * @return True if has a value
     */
    public boolean hasValue() {
        return this.hasValue;
    }

    /**
     * Method to check to see if this BillableField has a timestamp
     * @return True if has a timestamp
     */
    public boolean hasTimestamp() {
        return this.hasTimestamp;
    }

    /**
     * Method to check to see if this BillableField has a unit of measure
     * @return True if has a unit of measure
     */
    public boolean hasUnitOfMeasure() {
        return this.hasUnitOfMeasure;
    }

    /**
     * Returns the rate of the BillableField
     * @return rate
     */
    public String getRate() {
        return rate;
    }
}