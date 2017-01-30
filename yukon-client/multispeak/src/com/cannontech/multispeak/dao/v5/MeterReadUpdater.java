package com.cannontech.multispeak.dao.v5;

import com.cannontech.msp.beans.v5.multispeak.MeterReading;

/**
 * Utility callback that allows the implementer to encapsulate some manipulation
 * of a MeterReading object. It is assumed that some implementations of this will
 * aggregate other implementations of this interface.
 */
public interface MeterReadUpdater {
    /**
     * Applies some encapsulated manipulation to the MeterReading object.
     */
    public void update(MeterReading meterRead);
}
