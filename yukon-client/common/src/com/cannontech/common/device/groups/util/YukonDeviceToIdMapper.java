package com.cannontech.common.device.groups.util;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;

public class YukonDeviceToIdMapper implements ObjectMapper<YukonDevice, Integer> {
    public YukonDeviceToIdMapper() {
    }
    
    public Integer map(YukonDevice from) {
        return from.getDeviceId();
    }
}