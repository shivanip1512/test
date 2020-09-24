package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceMeterGroup;

public class VirtualMeter extends VirtualDevice implements IDeviceMeterGroup {

    private DeviceMeterGroup deviceMeterGroup;

    public VirtualMeter() {
            super(PaoType.VIRTUAL_METER);
    }

    @Override
    public DeviceMeterGroup getDeviceMeterGroup() {
            return deviceMeterGroup;
    }

    @Override
    public void setDeviceMeterGroup(DeviceMeterGroup dvMtrGrp_) {
            deviceMeterGroup = dvMtrGrp_;
    }

}
