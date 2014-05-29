package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceAddress;

public abstract class AddressBase extends RemoteBase {
    private DeviceAddress deviceAddress = null;

    public AddressBase(PaoType paoType) {
        super(paoType);
    }
    
    public Integer getAddress() {
        return getDeviceAddress().getMasterAddress();
    }

    public void setAddress(Integer newAddress) {
        getDeviceAddress().setMasterAddress(newAddress);
    }

    @Override
    public void add() throws SQLException {
        super.add();
        getDeviceAddress().add();
    }

    @Override
    public void delete() throws SQLException {
        if (!isPartialDelete) {
            getDeviceAddress().delete();
        }
        super.delete();
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getDeviceAddress().retrieve();
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getDeviceAddress().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceAddress().setDeviceID(deviceID);
    }

    @Override
    public void update() throws SQLException {
        super.update();
        getDeviceAddress().update();
    }

    public DeviceAddress getDeviceAddress() {
        if (deviceAddress == null) {
            deviceAddress = new DeviceAddress();
        }

        return deviceAddress;
    }

    public void setDeviceAddress(DeviceAddress deviceAddr) {
        this.deviceAddress = deviceAddr;
    }
}