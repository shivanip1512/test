package com.cannontech.common.bulk.mapper;

import com.cannontech.common.bulk.field.impl.UpdateableDevice;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;

public class UpdateableDeviceMapper implements ObjectMapper<UpdateableDevice, YukonDevice> {
    
    @Override
    public YukonDevice map(UpdateableDevice from)
            throws ObjectMappingException {
        return from.getDevice();
    }
}