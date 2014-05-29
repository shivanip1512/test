package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceTapPagingSettings;

public abstract class TapTerminalBase extends IEDBase {

    private DeviceTapPagingSettings deviceTapPagingSettings = null;

    public TapTerminalBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceTapPagingSettings().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getDeviceTapPagingSettingsDefaults().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getDeviceTapPagingSettings().delete();
        super.delete();
    }

    public DeviceTapPagingSettings getDeviceTapPagingSettings() {
        if (deviceTapPagingSettings == null) {
            deviceTapPagingSettings = new DeviceTapPagingSettings();
        }

        return deviceTapPagingSettings;
    }

    public DeviceTapPagingSettings getDeviceTapPagingSettingsDefaults() {

        getDeviceTapPagingSettings().setPagerNumber("None");
        return getDeviceTapPagingSettings();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getDeviceTapPagingSettings().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getDeviceTapPagingSettings().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceTapPagingSettings().setDeviceID(deviceID);
    }

    public void setDeviceTapPagingSettings(DeviceTapPagingSettings newValue) {
        this.deviceTapPagingSettings = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceTapPagingSettings().update();
    }
}