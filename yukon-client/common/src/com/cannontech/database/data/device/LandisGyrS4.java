package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class LandisGyrS4 extends IEDMeter {

    public LandisGyrS4() {
        super(PaoType.LANDISGYRS4);
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
    }
}
