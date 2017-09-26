package com.cannontech.database.data.capcontrol;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.capcontrol.DeviceCBC;

public abstract class CapBankController extends CapControlDeviceBase implements ICapBankController {
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

    public DeviceCBC getDeviceCBC() {
        if (deviceCBC == null) {
            deviceCBC = new DeviceCBC();
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

    public void setDeviceCBC(DeviceCBC newValue) {
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
