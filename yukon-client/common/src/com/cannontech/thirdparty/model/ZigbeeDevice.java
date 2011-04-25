package com.cannontech.thirdparty.model;

import com.cannontech.common.pao.YukonDevice;

public interface ZigbeeDevice extends YukonDevice {
    public int getZigbeeDeviceId();
    public String getZigbeeMacAddress();
}
