package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT310IDL extends MCTBase {

    public MCT310IDL() {
        super(PaoType.MCT310IDL);
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
