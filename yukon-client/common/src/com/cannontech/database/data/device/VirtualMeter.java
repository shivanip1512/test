package com.cannontech.database.data.device;

import java.sql.SQLException;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceMeterGroup;

public class VirtualMeter extends VirtualBase implements IDeviceMeterGroup {

    private DeviceMeterGroup deviceMeterGroup;

    public VirtualMeter() {
            super(PaoType.VIRTUAL_METER);
    }

    @Override public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceMeterGroup().setDeviceID(deviceID);
    }

    @Override
    public void add() throws SQLException {
        super.add();
        getDeviceMeterGroup().add();
    }

    @Override
    public void delete() throws SQLException {
        getDeviceMeterGroup().delete();
        super.delete();
    }

    @Override
    public void update() throws SQLException {
        getDeviceMeterGroup().update();
        super.update();
    }

    @Override
    public DeviceMeterGroup getDeviceMeterGroup() {
            if (deviceMeterGroup == null) {
                deviceMeterGroup = new DeviceMeterGroup();
            }
            return deviceMeterGroup;
    }

    @Override
    public void setDeviceMeterGroup(DeviceMeterGroup dvMtrGrp_) {
            deviceMeterGroup = dvMtrGrp_;
    }

}
