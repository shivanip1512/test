package com.cannontech.database.data.device;

import com.cannontech.database.db.device.DeviceAddress;

public class AddressBase extends RemoteBase {
    private DeviceAddress deviceAddress = null;

    public AddressBase() {
        super();
    }
    
    public Integer getAddress() {
        return getDeviceAddress().getMasterAddress();
    }

    public void setAddress(Integer newAddress) {
        getDeviceAddress().setMasterAddress(newAddress);
    }

    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceAddress().add();
    }

    public void delete() throws java.sql.SQLException {
        getDeviceAddress().delete();
        super.delete();
    }

    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceAddress().retrieve();
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceAddress().setDbConnection(conn);
    }

    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceAddress().setDeviceID(deviceID);
    }

    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceAddress().update();
    }

    public DeviceAddress getDeviceAddress() {
        if (deviceAddress == null)
            deviceAddress = new DeviceAddress();

        return deviceAddress;
    }

    public void setDeviceAddress(DeviceAddress deviceAddr) {
        this.deviceAddress = deviceAddr;
    }
}
