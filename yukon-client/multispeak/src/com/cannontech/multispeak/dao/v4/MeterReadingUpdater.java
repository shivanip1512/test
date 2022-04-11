package com.cannontech.multispeak.dao.v4;

import com.cannontech.msp.beans.v4.MeterReading;

/**
 * Utility callback that allows the implementer to encapsulate some manipulation
 * of a MeterReading object. It is assumed that some implementations of this will
 * aggregate other implementations of this interface.
 */
public interface MeterReadingUpdater {
    /**
     * Applies some encapsulated manipulation to the MeterRead object.
     */
    public void update(MeterReading meterRead);
}
