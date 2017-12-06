package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceParent;

public class CapBankControllerLogical extends CapBankController {
    
    private DeviceParent deviceParent = null;

    public CapBankControllerLogical() {
        super(PaoType.CBC_LOGICAL);
    }
    
    public Integer getParentDeviceId() {
        return getDeviceParent().getParentId();
    }

    public void setParentDeviceId(Integer parentId) {
        getDeviceParent().setParentId(parentId);
    }

    @Override
    public void add() throws SQLException {
        super.add();
        getDeviceParent().add();
    }

    @Override
    public void delete() throws SQLException {
        if (!isPartialDelete) {
            getDeviceParent().delete();
        }
        super.delete();
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getDeviceParent().retrieve();
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getDeviceParent().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceParent().setDeviceId(deviceID);
    }

    @Override
    public void update() throws SQLException {
        super.update();
        getDeviceParent().update();
    }

    public DeviceParent getDeviceParent() {
        if (deviceParent == null)
            deviceParent = new DeviceParent();
        return deviceParent;
    }

    public void setDeviceParent(DeviceParent deviceParent) {
        this.deviceParent = deviceParent;
    }
    
}