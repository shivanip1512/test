package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceIED;

public abstract class IEDBase extends RemoteBase {
    private DeviceIED deviceIED = null;

    // static strings for the SLAVE ADDRESS field
    public static final String SLAVE_STAND_ALONE = "Standalone";
    public static final String SLAVE_MASTER = "Master";
    public static final String SLAVE_SLAVE1 = "Slave1";
    public static final String SLAVE_SLAVE2 = "Slave2";
    public static final String SLAVE_SLAVE3 = "Slave3";
    public static final String SLAVE_SLAVE4 = "Slave4";

    public IEDBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceIED().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getDeviceIEDDefaults().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceIED().delete();
        super.delete();
    }

    public DeviceIED getDeviceIED() {
        if (deviceIED == null) {
            deviceIED = new DeviceIED();
        }

        return deviceIED;
    }

    public DeviceIED getDeviceIEDDefaults() {
        getDeviceIED().setPassword("0");
        getDeviceIED().setSlaveAddress(IEDBase.SLAVE_STAND_ALONE);

        return getDeviceIED();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceIED().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceIED().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceIED().setDeviceID(deviceID);
    }

    public void setDeviceIED(DeviceIED newValue) {
        this.deviceIED = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceIED().update();
    }
}