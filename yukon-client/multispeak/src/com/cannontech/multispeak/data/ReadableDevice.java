package com.cannontech.multispeak.data;

import java.util.Set;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.multispeak.deploy.service.MeterRead;

/**
 * Interface to be implemented for each type of billable device
 */
public interface ReadableDevice {
	
	/**
	 * Set of attributes that the device supports and that map to point data that the MeterRead has setters for.
	 */
	public Set<Attribute> getMeterReadCompatibleAttributes();

	/**
     * Populate the MeterRead object with meter and richPointData data.
     * @param meter
     * @param richPointData
     */
	public void populate(Meter meter, RichPointData richPointData);
	
    /**
     * Method to load the (MSP) MeterRead object with the data in PointData cache..
     * @param deviceID
     */
    public void populateWithCachedPointData(Meter meter);
    
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
