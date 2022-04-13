package com.cannontech.multispeak.data.v4;

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
}
