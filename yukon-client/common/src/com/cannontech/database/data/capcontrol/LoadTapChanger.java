package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;

import com.cannontech.core.dao.LtcDao;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.spring.YukonSpringHook;

public class LoadTapChanger extends DNPBase {

    public LoadTapChanger() {
        super();
    }
    
    public void delete() throws SQLException {
        /* Unnassign it from a substation bus first. */
        LtcDao ltcDao = YukonSpringHook.getBean(LtcDao.class);
        ltcDao.unassignLtc(getPAObjectID());
        
        super.delete();
    }
    
    public boolean isScanOne() {
        return getDeviceScanRateMap().containsKey(DeviceScanRate.TYPE_INTEGRITY);
    }
    
    public boolean isScanTwo() {
        return getDeviceScanRateMap().containsKey(DeviceScanRate.TYPE_EXCEPTION);
    }
}
