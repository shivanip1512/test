package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceRTC;

public abstract class RTCBase extends RemoteBase {

    DeviceRTC deviceRTC = null;

    // strings for the Listen Before Talk column
    public static final String LBT0 = "No LBT";
    public static final String LBT1 = "Wait for next slot";
    public static final String LBT2 = "Wait for freq. clear";
    public static final String LBT3 = "Override after 1 slot";

    public RTCBase(PaoType paoType) {
        super(paoType);

    }

    public void assignAddress(Integer newAddress) {
        getDeviceRTC().setRTCAddress(newAddress);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceRTC().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceRTC().delete();
        super.delete();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceRTC().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceRTC().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceRTC().setDeviceID(deviceID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceRTC().update();
    }

    public DeviceRTC getDeviceRTC() {
        if (deviceRTC == null) {
            deviceRTC = new DeviceRTC();
        }

        return deviceRTC;
    }

    public void setDeviceRTC(DeviceRTC deviceRTC) {
        this.deviceRTC = deviceRTC;
    }

    public void setLBTMode(String newMode) {
        if (newMode.compareTo(LBT0) == 0)
            getDeviceRTC().setLBTMode(new Integer(0));
        if (newMode.compareTo(LBT1) == 0)
            getDeviceRTC().setLBTMode(new Integer(1));
        if (newMode.compareTo(LBT2) == 0)
            getDeviceRTC().setLBTMode(new Integer(2));
        if (newMode.compareTo(LBT3) == 0)
            getDeviceRTC().setLBTMode(new Integer(3));
    }

    public static String getLBTModeString(Integer newMode) {
        String lbtFun = LBT0;

        if (newMode.intValue() == 1)
            lbtFun = LBT1;
        if (newMode.intValue() == 2)
            lbtFun = LBT2;
        if (newMode.intValue() == 3)
            lbtFun = LBT3;

        return lbtFun;
    }
}