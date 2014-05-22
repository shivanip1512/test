package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class LCUER extends LCUBase {

    public LCUER() {
        super(PaoType.LCU_ER);
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
