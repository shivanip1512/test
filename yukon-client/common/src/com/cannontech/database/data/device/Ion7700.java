package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceMeterGroup;

public class Ion7700 extends AddressBase implements IDeviceMeterGroup {
    private DeviceMeterGroup deviceMeterGroup = null;

    /**
     * Valid paoTypes are Ion 7700, Ion 7330, Ion 8300
     * @param paoType
     */
    public Ion7700(PaoType paoType) {
        super(paoType);
    }
    
    @Override
    public DeviceMeterGroup getDeviceMeterGroup() {
        if (deviceMeterGroup == null)
            deviceMeterGroup = new DeviceMeterGroup();

        return deviceMeterGroup;
    }

    @Override
    public void setDeviceMeterGroup(DeviceMeterGroup dvMtrGrp_) {
        deviceMeterGroup = dvMtrGrp_;
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceMeterGroup().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getDeviceMeterGroup().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceMeterGroup().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws java.sql.SQLException {

        super.deletePartial();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceMeterGroup().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceMeterGroup().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceMeterGroup().setDeviceID(deviceID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();

        getDeviceMeterGroup().update();
    }
}
