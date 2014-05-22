package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.database.db.device.DeviceMeterGroup;


public class IEDMeter extends IEDBase implements IDeviceMeterGroup {
    private DeviceMeterGroup deviceMeterGroup = null;
    private DeviceLoadProfile deviceLoadProfile = null;

    public IEDMeter(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceMeterGroup().add();
        getDeviceLoadProfile().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        // getDeviceMeterGroupDefaults().add();
        getDeviceMeterGroup().add();
        getDeviceLoadProfile().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceMeterGroup().delete();
        getDeviceLoadProfile().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws java.sql.SQLException {
        super.deletePartial();
    }

    public DeviceLoadProfile getDeviceLoadProfile() {
        if (deviceLoadProfile == null)
            deviceLoadProfile = new DeviceLoadProfile();

        return deviceLoadProfile;
    }

    @Override
    public DeviceMeterGroup getDeviceMeterGroup() {
        if (deviceMeterGroup == null)
            deviceMeterGroup = new DeviceMeterGroup();

        return deviceMeterGroup;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceMeterGroup().retrieve();
        getDeviceLoadProfile().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getDeviceMeterGroup().setDbConnection(conn);
        getDeviceLoadProfile().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceMeterGroup().setDeviceID(deviceID);
        getDeviceLoadProfile().setDeviceID(deviceID);
    }

    public void setDeviceLoadProfile(DeviceLoadProfile newValue) {
        deviceLoadProfile = newValue;

    }

    @Override
    public void setDeviceMeterGroup(DeviceMeterGroup newValue) {
        this.deviceMeterGroup = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceMeterGroup().update();
        getDeviceLoadProfile().update();
    }
}
