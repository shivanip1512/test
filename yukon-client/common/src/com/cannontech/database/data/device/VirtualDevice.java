package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class VirtualDevice extends DeviceBase {

    public VirtualDevice() {
        super(PaoType.VIRTUAL_SYSTEM);
    }
}