package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.device.CrfAddress;

public class CrfBase extends DeviceBase implements IDeviceMeterGroup {
    private CrfAddress crfAddress = null;
    private DeviceMeterGroup deviceMeterGroup = null;
    
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceMeterGroup().setDeviceID(deviceID);
        getCrfAddress().setDeviceID(deviceID);
    }

    @Override
    public void add() throws SQLException {
        super.add();
        getDeviceMeterGroup().add();
        getCrfAddress().add();
    }
    
    @Override
    public void delete() throws SQLException {
        getDeviceMeterGroup().delete();
        getCrfAddress().delete();
        super.delete();
    }
    
    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getDeviceMeterGroup().retrieve();
        getCrfAddress().retrieve();
    }
    
    @Override
    public void update() throws SQLException {
        super.update();
        getDeviceMeterGroup().update();
        getCrfAddress().update();
    }
    
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getCrfAddress().setDbConnection(conn);
        getDeviceMeterGroup().setDbConnection(conn);
    }

    @Override
    public DeviceMeterGroup getDeviceMeterGroup() {
        if( deviceMeterGroup == null ) {
            deviceMeterGroup = new DeviceMeterGroup();
        }
        return deviceMeterGroup;
    }

    @Override
    public void setDeviceMeterGroup(DeviceMeterGroup deviceMeterGroup) {
        this.deviceMeterGroup = deviceMeterGroup;
    }

    public CrfAddress getCrfAddress() {
        if(crfAddress == null) {
            crfAddress = new CrfAddress();
        }
        return crfAddress;
    }

    public void setCrfAddress(CrfAddress rfmAddress) {
        this.crfAddress = rfmAddress;
    }
}