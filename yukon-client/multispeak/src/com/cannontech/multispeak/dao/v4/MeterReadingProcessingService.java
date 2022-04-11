package com.cannontech.multispeak.dao.v4;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v4.MeterReading;

public interface MeterReadingProcessingService {
    
    /**
     * Simple helper to create a blank MeterReading for a given Meter.
     */
    public MeterReading createMeterReading(YukonMeter meter);

    /**
     * This is an immediate version of the buildMeterReadUpdater that updates a MeterReading
     * object directly with the given value for the given attribute. This method
     * is not appropriate in a multi-threaded environment when the MeterRead may
     * be shared across threads.
     */
    public void updateMeterReading(MeterReading reading, BuiltInAttribute attribute,
                         PointValueHolder pointValueHolder);
}
