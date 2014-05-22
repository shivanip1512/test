package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class RepeaterBase extends CarrierBase {

    public RepeaterBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();

    }

    @Override
    public void delete() throws java.sql.SQLException {
        super.delete();
    }

    @Override
    public void deletePartial() throws java.sql.SQLException {
        super.deletePartial();

    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceCarrierSettings().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceCarrierSettings().setDeviceID(deviceID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
    }
}
