package com.cannontech.database.data.device;

import com.cannontech.database.data.pao.DeviceClasses;

public class XML extends RemoteBase {

    private String deviceAddress;
    
    public XML() {
        super();
        setDeviceClass( DeviceClasses.STRING_CLASS_TRANSMITTER );
    }
    
    public void setDbConnection(java.sql.Connection conn) 
    {
        super.setDbConnection(conn);
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
    
}
