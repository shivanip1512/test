package com.cannontech.multispeak.data.v4;

import java.util.Date;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.msp.beans.v4.MeterReading;

/**
 * Interface to be implemented for each type of billable device
 */
public interface ReadableDevice {

    /**
     * Returns the (MSP) meter reading object for this readable device.
     * @return (MSP) MeterReading object
     */
    public MeterReading getMeterReading();
    
    /**
     * Set the meterNumber of the readable device
     * @param meterNumber
     */
    public void setMeterNumber(String meterNumber);
    
    /**
     * Returns true if any read data has been populated
     * @return
     */
    public boolean isPopulated();
    
    /**
     * Method to load the (MSP) MeterRead object with the data in PointData cache..
     * @param deviceID
     */
    public void populateWithPointData(int deviceID);
    
    /**
     * Method to load the (MSP) MeterRead object with data collected from the database or physical reads.
     * 
     * @param pointIdentifer
     * @param uomID          - the point's unit of measure id
     * @param dateTime       - the Date of the reading for the point.
     * @param value          - the value of the reading for the point
     */
    public void populate(PointIdentifier pointIdentifier, Date dateTime, Double value);

}
