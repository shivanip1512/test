package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceAddress;

public class CCU721 extends RemoteBase {
    private DeviceAddress deviceAddress = null;

    public CCU721() {
        super(PaoType.CCU721);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceAddress().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();

        getDeviceAddress().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceAddress().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws java.sql.SQLException {

        super.deletePartial();

    }

    public DeviceAddress getDeviceAddress() {
        if (deviceAddress == null) {
            deviceAddress = new DeviceAddress();
        }

        return deviceAddress;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {

        super.retrieve();
        getDeviceAddress().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getDeviceAddress().setDbConnection(conn);
    }

    public void setDeviceAddress(DeviceAddress newValue) {
        this.deviceAddress = newValue;
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);

        getDeviceAddress().setDeviceID(deviceID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceAddress().update();
    }
}
