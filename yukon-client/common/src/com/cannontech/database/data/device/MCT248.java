package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT248 extends MCTBase {

    public MCT248() {
        super(PaoType.MCT248);
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
