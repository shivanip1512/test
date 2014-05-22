package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class LCU415 extends LCUBase {

    public LCU415() {
        super(PaoType.LCU415);
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
