package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class RTUILEX extends RTUBase {

    public RTUILEX() {
        super(PaoType.RTUILEX);
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
