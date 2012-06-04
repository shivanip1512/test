package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.database.db.device.DeviceMeterGroup;

public class RfnMeterBase extends RfnBase implements IDeviceMeterGroup {
    
    private DeviceMeterGroup deviceMeterGroup = null;
    
    public void setDeviceID(Integer deviceId) {
        super.setDeviceID(deviceId);
        getDeviceMeterGroup().setDeviceID(deviceId);
    }
    
    @Override
    public void add() throws SQLException {
        super.add();
        getDeviceMeterGroup().add();
    }
    
    @Override
    public void addPartial() throws SQLException {
        super.addPartial();
        getDeviceMeterGroup().add();
    }
    
    @Override
    public void delete() throws SQLException {
        getDeviceMeterGroup().delete();
        super.delete();
    }
    
    @Override
    public void deletePartial() throws SQLException {
        super.deletePartial();
    }
    
    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getDeviceMeterGroup().retrieve();
    }
    
    @Override
    public void update() throws SQLException {
        super.update();
        getDeviceMeterGroup().update();
    }
    
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
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
    
}