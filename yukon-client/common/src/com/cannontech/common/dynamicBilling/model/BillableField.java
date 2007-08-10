package com.cannontech.common.dynamicBilling.model;

/**
 * Enumeration of all of the possible field types a BillableDevice may have
 */
public enum BillableField {
    meterNumber(true, false, false, false), 
    meterPositionNumber(true, false, false, false), 
    accountNumber(true, false, false, false), 
    paoName(true, false, false, false), 
    address(true, false, false, false), 
    totalConsumption(true, true, true, true), 
    totalPeakDemand(true, true, true, true), 
    rateAConsumption(true, true, true, true), 
    rateBConsumption(true, true, true, true), 
    rateCConsumption(true, true, true, true), 
    rateDConsumption(true, true, true, true), 
    rateADemand(true, true, true, true), 
    rateBDemand(true, true, true, true), 
    rateCDemand(true, true, true, true), 
    rateDDemand(true, true, true, true);

    private boolean hasData = false;
    private boolean hasValue = false;
    private boolean hasTimestamp = false;
    private boolean hasUnitOfMeasure = false;

    BillableField(boolean hasData, boolean hasValue, boolean hasTimestamp, boolean hasUnitOfMeasure) {
        this.hasData = hasData;
        this.hasValue = hasValue;
        this.hasTimestamp = hasTimestamp;
        this.hasUnitOfMeasure = hasUnitOfMeasure;
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
}