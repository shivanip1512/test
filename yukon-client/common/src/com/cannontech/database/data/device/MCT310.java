package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT310 extends MCTBase {

    public MCT310() {
        super(PaoType.MCT310);
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
