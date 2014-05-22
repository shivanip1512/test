package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class Focus extends IEDMeter {

    public Focus() {
        super(PaoType.FOCUS);
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
