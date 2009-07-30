package com.cannontech.common.bulk.mapper;

import com.cannontech.common.bulk.field.impl.UpdateableDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;

public class UpdateableDeviceMapper implements ObjectMapper<UpdateableDevice, SimpleDevice> {
    
    @Override
    public SimpleDevice map(UpdateableDevice from)
            throws ObjectMappingException {
        return from.getDevice();
    }
}