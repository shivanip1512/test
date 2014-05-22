package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT210 extends MCTBase {

    public MCT210() {
        super(PaoType.MCT210);
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
