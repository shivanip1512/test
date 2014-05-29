package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceSeries5RTU;
import com.cannontech.database.db.device.DeviceVerification;

public abstract class Series5Base extends RemoteBase {

    // we use the DeviceAddress table to store the address
    private DeviceAddress deviceAddress = null;
    private DeviceSeries5RTU series5RTU = null;
    // DeviceVerification entry that references itself (identical IDs)
    private DeviceVerification lmiVerification = null;

    public Series5Base(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getSeries5().add();
        getSeries5RTU().add();
        getVerification().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getSeries5RTU().delete();
        getSeries5().delete();
        getVerification().delete();
        super.delete();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getSeries5().retrieve();
        getSeries5RTU().retrieve();
        getVerification().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getSeries5().setDbConnection(conn);
        getSeries5RTU().setDbConnection(conn);
        getVerification().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getSeries5().setDeviceID(deviceID);
        getSeries5RTU().setDeviceID(deviceID);

        // funky
        getVerification().setReceiverID(deviceID);
        getVerification().setTransmitterID(deviceID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getSeries5().update();
        getSeries5RTU().update();
        getVerification().update();
    }

    public DeviceAddress getSeries5() {
        if (deviceAddress == null)
            deviceAddress = new DeviceAddress();

        return deviceAddress;
    }

    public void setSeries5(DeviceAddress series5) {
        this.deviceAddress = series5;
    }

    public DeviceSeries5RTU getSeries5RTU() {
        if (series5RTU == null) {
            series5RTU = new DeviceSeries5RTU();
        }

        return series5RTU;
    }

    public void setSeries5RTU(DeviceSeries5RTU series5RTU) {
        this.series5RTU = series5RTU;
    }

    public DeviceVerification getVerification() {
        if (lmiVerification == null) {
            lmiVerification = new DeviceVerification();
        }

        return lmiVerification;
    }

    public void setVerification(DeviceVerification verify) {
        this.lmiVerification = verify;
    }
}