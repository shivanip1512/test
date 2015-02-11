package com.cannontech.multispeak.dao;

import com.cannontech.msp.beans.v3.MeterRead;



/**
 * Utility callback that allows the implementer to encapsulate some manipulation
 * of a MetrRead object. It is assumed that some implementations of this will
 * aggregate other implementations of this interface.
 */
public interface MeterReadUpdater {
    /**
     * Applies some encapsulated manipulation to the MeterRead object.
     */
    public void update(MeterRead meterRead);
}
