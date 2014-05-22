package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class CCU711 extends CCUBase {

    public CCU711() {
        super(PaoType.CCU711);
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
