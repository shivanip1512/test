package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class LCUT3026 extends LCUBase {

    public LCUT3026() {
        super(PaoType.LCU_T3026);
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
