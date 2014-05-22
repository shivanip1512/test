package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class LMT2 extends MCTBase {

    public LMT2() {
        super(PaoType.LMT_2);
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
