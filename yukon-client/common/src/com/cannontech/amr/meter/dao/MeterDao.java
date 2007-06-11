package com.cannontech.amr.meter.dao;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface MeterDao extends StandardDaoOperations<Meter> {
    public String getFormattedDeviceName(Meter device);
}
