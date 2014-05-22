package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT250 extends MCTBase {

    public MCT250() {
        super(PaoType.MCT250);
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
