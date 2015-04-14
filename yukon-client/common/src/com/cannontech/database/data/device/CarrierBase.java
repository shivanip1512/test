package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.DBCopiable;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceRoutes;

public abstract class CarrierBase extends TwoWayDevice implements DBCopiable {

    private DeviceRoutes deviceRoutes = null;

    private DeviceCarrierSettings deviceCarrierSettings = null;

    public CarrierBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public Integer getAddress() {
        return getDeviceCarrierSettings().getAddress();
    }

    @Override
    public void setAddress(Integer newAddress) {
        getDeviceCarrierSettings().setAddress(newAddress);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();

        getDeviceCarrierSettings().add();
        getDeviceRoutes().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();

        getDeviceCarrierSettings().add();
        getDeviceRoutes().add();
    }
    
    @Override
    public void deletePartial() throws java.sql.SQLException {
        getDeviceCarrierSettings().delete();
        getDeviceRoutes().delete();
        super.deletePartial();
    }

    @Override
    public void delete() throws java.sql.SQLException {

        getDeviceRoutes().delete();
        getDeviceCarrierSettings().delete();
        super.delete();
    }

    public DeviceCarrierSettings getDeviceCarrierSettings() {
        if (deviceCarrierSettings == null) {
            deviceCarrierSettings = new DeviceCarrierSettings();
        }

        return deviceCarrierSettings;
    }

    public DeviceRoutes getDeviceRoutes() {
        if (deviceRoutes == null) {
            deviceRoutes = new DeviceRoutes();
        }

        return deviceRoutes;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {

        super.retrieve();
        getDeviceCarrierSettings().retrieve();
        getDeviceRoutes().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceCarrierSettings().setDbConnection(conn);
        getDeviceRoutes().setDbConnection(conn);
    }

    public void setDeviceCarrierSettings(DeviceCarrierSettings newValue) {
        this.deviceCarrierSettings = newValue;
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceCarrierSettings().setDeviceID(deviceID);
        getDeviceRoutes().setDeviceID(deviceID);
    }

    public void setDeviceRoutes(DeviceRoutes newValue) {
        this.deviceRoutes = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();

        getDeviceCarrierSettings().update();
        getDeviceRoutes().update();
    }
}