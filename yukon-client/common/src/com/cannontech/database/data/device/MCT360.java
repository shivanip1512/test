package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class MCT360 extends MCTIEDBase {

    public MCT360() {
        super(PaoType.MCT360);
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
