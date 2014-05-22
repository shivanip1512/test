package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT310IL extends MCTBase {

    public MCT310IL() {
        super(PaoType.MCT310IL);
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
