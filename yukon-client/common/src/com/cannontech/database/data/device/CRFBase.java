package com.cannontech.database.data.device;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.device.CRFAddress;

public class CRFBase extends DeviceBase implements IDeviceMeterGroup {
    private CRFAddress rfmAddress = null;
    private DeviceMeterGroup deviceMeterGroup = null;
    
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceMeterGroup().setDeviceID(deviceID);
        getRFMAddress().setDeviceID(deviceID);
    }

    @Override
    public void add() throws SQLException {
        super.add();
        getDeviceMeterGroup().add();
        getRFMAddress().add();
    }
    
    @Override
    public void delete() throws SQLException {
        getDeviceMeterGroup().delete();
        getRFMAddress().delete();
        super.delete();
    }
    
    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getDeviceMeterGroup().retrieve();
        getRFMAddress().retrieve();
    }
    
    @Override
    public void update() throws SQLException {
        super.update();
        getDeviceMeterGroup().update();
        getRFMAddress().update();
    }
    
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getRFMAddress().setDbConnection(conn);
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

    public CRFAddress getRFMAddress() {
        if(rfmAddress == null) {
            rfmAddress = new CRFAddress();
        }
        return rfmAddress;
    }

    public void setRFMAddress(CRFAddress rfmAddress) {
        this.rfmAddress = rfmAddress;
    }
}