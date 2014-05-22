package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class TCU5500 extends TCUBase {

    public TCU5500() {
        super(PaoType.TCU5500);
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
