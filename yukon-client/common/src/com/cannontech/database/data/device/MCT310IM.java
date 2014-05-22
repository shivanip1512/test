package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT310IM extends MCTBase {

    public MCT310IM() {
        super(PaoType.MCT310IM);
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
