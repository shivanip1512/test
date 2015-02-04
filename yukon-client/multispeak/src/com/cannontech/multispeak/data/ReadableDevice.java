package com.cannontech.multispeak.data;

import java.util.Date;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.multispeak.deploy.service.MeterRead;

/**
 * Interface to be implemented for each type of billable device
 */
public interface ReadableDevice {

    /**
     * Method to load the (MSP) MeterRead object with data collected from the database or physical reads.
     * @param pointIdentifer
     * @param uomID - the point's unit of measure id
     * @param dateTime - the Date of the reading for the point.
     * @param value - the value of the reading for the point
     */
    public void populate(PointIdentifier pointIdentifier, Date dateTime, Double value);

    /**
     * Method to load the (MSP) MeterRead object with the data in PointData cache..
     * @param deviceID
     */
    public void populateWithPointData(int deviceID);
    /**
     * Returns true if any read data has been populated
     * @return
     */
    public boolean isPopulated();
    
    /**
     * Set the value of populated
     * @param value
     */
    public void setPopulated(boolean value);
    /**
     * Returns the (MSP) meter read object for this readable device.
     * @return (MSP) MeterRead object
     */
    public MeterRead getMeterRead();
    
    /**
     * Set the meterNumber of the readable debice
     * @param meterNumber
     */
    public void setMeterNumber(String meterNumber);
}
