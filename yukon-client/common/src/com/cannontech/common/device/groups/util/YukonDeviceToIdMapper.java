package com.cannontech.common.device.groups.util;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;

public class YukonDeviceToIdMapper implements ObjectMapper<SimpleDevice, Integer> {
    public YukonDeviceToIdMapper() {
    }
    
    public Integer map(SimpleDevice from) {
        return from.getDeviceId();
    }
}