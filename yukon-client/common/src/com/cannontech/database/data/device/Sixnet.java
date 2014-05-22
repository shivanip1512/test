package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class Sixnet extends IEDMeter {

    public Sixnet() {
        super(PaoType.SIXNET);
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
