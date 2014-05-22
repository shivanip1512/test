package com.cannontech.database.data.capcontrol;

import com.cannontech.common.pao.PaoType;

public abstract class CapControlDeviceBase extends
        com.cannontech.database.data.device.DeviceBase {

    public CapControlDeviceBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        super.delete();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
    }
}
