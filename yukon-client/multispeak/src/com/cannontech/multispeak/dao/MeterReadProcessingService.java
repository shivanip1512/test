package com.cannontech.multispeak.dao;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v3.MeterRead;


public interface MeterReadProcessingService {
    /**
     * Return an immutable object that can be used at the callers discretion
     * to update a MeterRead object with the given value for the given attribute.
     * The primary purpose of this is to allow the caller to asynchronously process
     * the point values into an object that can easily be stored (and can even be
     * aggregated or chained) and then processed later in a synchronous manner.
     */
    public MeterReadUpdater buildMeterReadUpdater(BuiltInAttribute attribute, PointValueHolder pointValueHolder);

    /**
     * Simple helper to create a blank MeterRead for a given Meter.
     */
    public MeterRead createMeterRead(YukonMeter meter);

    /**
     * This is an immediate version of the buildMeterReadUpdater that updates a MeterRead
     * object directly with the given value for the given attribute. This method
     * is not appropriate in a multi-threaded environment when the MeterRead may
     * be shared across threads.
     * @param paoType 
     */
    public void updateMeterRead(MeterRead reading, BuiltInAttribute attribute, PointValueHolder pointValueHolder);
}
