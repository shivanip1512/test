package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceTNPPSettings;

public class TNPPTerminal extends IEDBase {

    private DeviceTNPPSettings deviceTNPPSettings = null;

public TNPPTerminal() {
    super(PaoType.TNPP_TERMINAL);
}

@Override
public void add() throws java.sql.SQLException {
    super.add();
    getDeviceTNPPSettings().add();
}
@Override
public void addPartial() throws java.sql.SQLException {
    
    super.addPartial();
    getDeviceTNPPSettingsDefaults().add();
}

@Override
public void delete() throws java.sql.SQLException{
    getDeviceTNPPSettings().delete();
    super.delete();
}
@Override
public void deletePartial() throws java.sql.SQLException {
    super.deletePartial();
}

public DeviceTNPPSettings getDeviceTNPPSettings() {
    if ( deviceTNPPSettings == null )
        deviceTNPPSettings = new DeviceTNPPSettings();
        
    return deviceTNPPSettings;
}

public DeviceTNPPSettings getDeviceTNPPSettingsDefaults()
{
    return getDeviceTNPPSettings();
}

@Override
public void retrieve() throws java.sql.SQLException{
    super.retrieve();

    getDeviceTNPPSettings().retrieve();
}

@Override
public void setDbConnection(java.sql.Connection conn) 
{
    super.setDbConnection(conn);

    getDeviceTNPPSettings().setDbConnection(conn);
}

@Override
public void setDeviceID(Integer deviceID) {
    super.setDeviceID(deviceID);
    getDeviceTNPPSettings().setDeviceId(deviceID);
}

public void setDeviceTNPPSettings(DeviceTNPPSettings newValue) {
    this.deviceTNPPSettings = newValue;
}

@Override
public void update() throws java.sql.SQLException{
    super.update();
    getDeviceTNPPSettings().update();
}
}
