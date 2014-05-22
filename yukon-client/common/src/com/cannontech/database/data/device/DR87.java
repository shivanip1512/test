package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class DR87 extends IEDMeter {

    public DR87() {
        super(PaoType.DR_87);
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
