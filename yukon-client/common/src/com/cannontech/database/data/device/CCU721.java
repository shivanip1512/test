package com.cannontech.database.data.device;

import java.sql.Connection;

/**
 * CCU 721
 */
public class CCU721 extends CCUBase {
    public CCU721() {
        super();
    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
    }

    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
    }
}
