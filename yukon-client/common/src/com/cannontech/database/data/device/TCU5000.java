package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class TCU5000 extends TCUBase {

    public TCU5000() {
        super(PaoType.TCU5000);
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
