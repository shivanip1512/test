package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT318L extends MCTBase {

    public MCT318L() {
        super(PaoType.MCT318L);
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
