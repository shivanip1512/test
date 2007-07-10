package com.cannontech.amr.deviceread.dao;

import java.util.Set;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * @author tmack
 *
 */
public interface MeterReadService {
    /**
     * This method will determine which commands need to be sent to read
     * the requested attributes and then use the CommandRequestExecutor
     * to actually execute the commands and return a result.
     * @param device
     * @param attribute
     */
    public CommandResultHolder readMeter(Meter device, Set<Attribute> attribute, LiteYukonUser user);

}
