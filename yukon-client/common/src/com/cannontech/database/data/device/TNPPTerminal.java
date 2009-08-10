package com.cannontech.database.data.device;

import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.db.device.DeviceTNPPSettings;

public class TNPPTerminal extends IEDBase {

    private DeviceTNPPSettings deviceTNPPSettings = null;

public TNPPTerminal() {
    super();
    setDeviceClass( DeviceClasses.STRING_CLASS_TRANSMITTER );
}

public void add() throws java.sql.SQLException {
    super.add();
    getDeviceTNPPSettings().add();
}
public void addPartial() throws java.sql.SQLException {
    
    super.addPartial();
    getDeviceTNPPSettingsDefaults().add();
}

public void delete() throws java.sql.SQLException{
    getDeviceTNPPSettings().delete();
    super.delete();
}
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

public void retrieve() throws java.sql.SQLException{
    super.retrieve();

    getDeviceTNPPSettings().retrieve();
}

public void setDbConnection(java.sql.Connection conn) 
{
    super.setDbConnection(conn);

    getDeviceTNPPSettings().setDbConnection(conn);
}

public void setDeviceID(Integer deviceID) {
    super.setDeviceID(deviceID);
    getDeviceTNPPSettings().setDeviceId(deviceID);
}

public void setDeviceTNPPSettings(DeviceTNPPSettings newValue) {
    this.deviceTNPPSettings = newValue;
}

public void update() throws java.sql.SQLException{
    super.update();
    getDeviceTNPPSettings().update();
}
}
