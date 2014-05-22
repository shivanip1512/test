package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT213 extends MCTBase {

    public MCT213() {
        super(PaoType.MCT213);
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
