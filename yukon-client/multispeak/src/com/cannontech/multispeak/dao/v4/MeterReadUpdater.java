package com.cannontech.multispeak.dao.v4;

import com.cannontech.msp.beans.v4.MeterReading;

public interface MeterReadUpdater {
    /**
     * Applies some encapsulated manipulation to the MeterReading object.
     */
    public void update(MeterReading meterRead);


}
