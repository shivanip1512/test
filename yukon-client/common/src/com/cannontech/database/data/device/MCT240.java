package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT240 extends MCTBase {

    public MCT240() {
        super(PaoType.MCT240);
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
