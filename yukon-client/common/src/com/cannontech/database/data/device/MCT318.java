package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT318 extends MCTBase {

    public MCT318() {
        super(PaoType.MCT318);
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
