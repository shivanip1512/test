package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class CCU710A extends CCUBase {

    public CCU710A() {
        super(PaoType.CCU710A);
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
