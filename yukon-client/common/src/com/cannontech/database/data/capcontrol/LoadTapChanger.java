package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;

import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.db.device.DeviceScanRate;

public class LoadTapChanger extends DNPBase {

    public LoadTapChanger() {
        super();
    }
    
    public void delete() throws SQLException {
        /* Unnassign it from a substation bus first. */
        delete("CCSubstationBusToLTC", "ltcId", getPAObjectID());
        
        super.delete();
    }
    
    public boolean isScanOne() {
        return getDeviceScanRateMap().containsKey(DeviceScanRate.TYPE_INTEGRITY);
    }
    
    public void setScanOne(boolean b){}
    
    public boolean isScanTwo() {
        return getDeviceScanRateMap().containsKey(DeviceScanRate.TYPE_EXCEPTION);
    }
    
    public void setScanTwo(boolean b){}
}
