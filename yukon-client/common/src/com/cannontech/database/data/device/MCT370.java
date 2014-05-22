package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT370 extends MCTIEDBase {

    public MCT370() {
        super(PaoType.MCT370);
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
