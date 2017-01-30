package com.cannontech.multispeak.dao.v5;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v5.multispeak.MeterReading;


public interface MeterReadProcessingService {
    /**
     * Return an immutable object that can be used at the callers discretion
     * to update a MeterRead object with the given value for the given attribute.
     * The primary purpose of this is to allow the caller to asynchronously process
     * the point values into an object that can easily be stored (and can even be
     * aggregated or chained) and then processed later in a synchronous manner.
     */
    public MeterReadUpdater buildMeterReadUpdater(BuiltInAttribute attribute,
                                           PointValueHolder pointValueHolder);

    /**
     * Simple helper to create a blank MeterRead for a given Meter.
     */
    public MeterReading createMeterRead(YukonMeter meter);

    /**
     * This is an immediate version of the buildMeterReadUpdater that updates a MeterRead
     * object directly with the given value for the given attribute. This method
     * is not appropriate in a multi-threaded environment when the MeterRead may
     * be shared across threads.
     */
    public void updateMeterRead(MeterReading reading, BuiltInAttribute attribute,
                         PointValueHolder pointValueHolder);
}
