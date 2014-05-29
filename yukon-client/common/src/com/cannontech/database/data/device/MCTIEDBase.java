package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceMCTIEDPort;

public abstract class MCTIEDBase extends MCTBase {
    private DeviceMCTIEDPort deviceMCTIEDPort = null;

    public MCTIEDBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceMCTIEDPort().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getDeviceMCTIEDPortDefaults().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceMCTIEDPort().delete();
        super.delete();
    }

    public DeviceMCTIEDPort getDeviceMCTIEDPort() {
        if (deviceMCTIEDPort == null) {
            deviceMCTIEDPort = new DeviceMCTIEDPort();
        }

        return deviceMCTIEDPort;
    }

    public DeviceMCTIEDPort getDeviceMCTIEDPortDefaults() {

        getDeviceMCTIEDPort().setConnectedIED("None");
        getDeviceMCTIEDPort().setIEDScanRate(new Integer(60));
        getDeviceMCTIEDPort().setDefaultDataClass(new Integer(0));
        getDeviceMCTIEDPort().setDefaultDataOffset(new Integer(0));
        getDeviceMCTIEDPort().setPassword("None");
        getDeviceMCTIEDPort().setRealTimeScan(new Character('N'));

        return getDeviceMCTIEDPort();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceMCTIEDPort().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceMCTIEDPort().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceMCTIEDPort().setDeviceID(deviceID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceMCTIEDPort().update();
    }
}