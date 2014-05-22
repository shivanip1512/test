package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT310ID extends MCTBase {

    public MCT310ID() {
        super(PaoType.MCT310ID);
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
