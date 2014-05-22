package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT310CT extends MCTBase {

    public MCT310CT() {
        super(PaoType.MCT310CT);
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
