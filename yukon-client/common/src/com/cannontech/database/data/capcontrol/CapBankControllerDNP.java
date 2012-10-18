package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.db.DBCopiable;
import com.cannontech.database.db.capcontrol.DeviceCBC;

public class CapBankControllerDNP extends DNPBase implements DBCopiable, ICapBankController {
    private DeviceCBC deviceCBC = null;
    
    public Integer getSerialNumber() {
        return getDeviceCBC().getSerialNumber();
    }

    public void setSerialNumber(Integer serialNumber_) {
        getDeviceCBC().setSerialNumber(serialNumber_);
    }

    public Integer getRouteId() {
        return getDeviceCBC().getRouteID();
    }

    public void setRouteId(Integer routeId_) {
        getDeviceCBC().setRouteID(routeId_);
    }

    public void setCommID(Integer comID) {
        getDeviceDirectCommSettings().setPortID(comID);
    }

    public Integer getCommID() {
        return getDeviceDirectCommSettings().getPortID();
    }

    public void add() throws SQLException {
        super.add();
        getDeviceCBC().add();
    }
    
    public void delete() throws SQLException {
        if (!isPartialDelete) {
            getDeviceCBC().delete();
        }
        super.delete();
    }

    public void retrieve() throws SQLException {
        super.retrieve();
        getDeviceCBC().retrieve();

    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getDeviceCBC().setDbConnection(conn);

    }

    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceCBC().setDeviceID(deviceID);
    }

    public void update() throws SQLException {
        super.update();
        getDeviceCBC().update();
    }

    public DeviceCBC getDeviceCBC() {
        if (deviceCBC == null)
            deviceCBC = new DeviceCBC();
        return deviceCBC;
    }

    public void setDeviceCBC(DeviceCBC deviceCBC) {
        this.deviceCBC = deviceCBC;
    }
}