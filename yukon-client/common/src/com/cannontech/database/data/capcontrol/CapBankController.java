package com.cannontech.database.data.capcontrol;

import com.cannontech.common.pao.PaoType;

public abstract class CapBankController extends CapControlDeviceBase implements
        com.cannontech.database.db.DBCopiable, ICapBankController {
    private com.cannontech.database.db.capcontrol.DeviceCBC deviceCBC = null;

    public CapBankController(PaoType paoType) {
        super(paoType);
    }

    @Override
    public Integer getAddress() {
        return getDeviceCBC().getSerialNumber();
    }

    @Override
    public void setAddress(Integer newAddress) {
        getDeviceCBC().setSerialNumber(newAddress);
    }

    @Override
    public void setCommID(Integer comID) {
        getDeviceCBC().setRouteID(comID);
    }

    @Override
    public Integer getCommID() {
        return getDeviceCBC().getRouteID();
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceCBC().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getDeviceCBC().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceCBC().delete();
        super.delete();
    }

    public com.cannontech.database.db.capcontrol.DeviceCBC getDeviceCBC() {
        if (deviceCBC == null) {
            deviceCBC = new com.cannontech.database.db.capcontrol.DeviceCBC();
        }

        return deviceCBC;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceCBC().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceCBC().setDbConnection(conn);
    }

    public void setDeviceCBC(
            com.cannontech.database.db.capcontrol.DeviceCBC newValue) {
        this.deviceCBC = newValue;
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceCBC().setDeviceID(deviceID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceCBC().update();
    }
}
