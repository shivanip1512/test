package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceIDLCRemote;

public abstract class IDLCBase extends RemoteBase {
    private DeviceIDLCRemote deviceIDLCRemote = null;

    public IDLCBase(PaoType paoType) {
        super(paoType);
    }

    public Integer getAddress() {
        return getDeviceIDLCRemote().getAddress();
    }

    public void setAddress(Integer newAddress) {
        getDeviceIDLCRemote().setAddress(newAddress);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceIDLCRemote().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getDeviceIDLCRemote().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceIDLCRemote().delete();
        super.delete();
    }

    public DeviceIDLCRemote getDeviceIDLCRemote() {
        if (deviceIDLCRemote == null) {
            deviceIDLCRemote = new DeviceIDLCRemote();
        }

        return deviceIDLCRemote;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceIDLCRemote().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceIDLCRemote().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceIDLCRemote().setDeviceID(deviceID);
    }

    public void setDeviceIDLCRemote(DeviceIDLCRemote newValue) {
        this.deviceIDLCRemote = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceIDLCRemote().update();
    }
}