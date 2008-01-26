package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.multispeak.deploy.service.MeterRead;

public interface MspMeterReadDao {

    public MeterRead getMeterRead(Meter meter, String uniqueKey);
    
    public MeterRead[] getMeterRead( List<Meter> meters, String uniqueKey);
}
