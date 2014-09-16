package com.cannontech.amr.deviceread.dao;

import java.util.Set;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PlcDeviceAttributeReadService {
    
    
    /**
     * Initiates a simple read for the attributes and returns the CommandResultHolder that
     * corresponds to the Command Requests that were submitted.
     */
    public CommandResultHolder readMeter(YukonDevice device, Set<? extends Attribute> attributes, DeviceRequestType type, LiteYukonUser user);
    
}
