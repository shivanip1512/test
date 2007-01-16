package com.cannontech.database.data.device;

import java.sql.Connection;

/**
 * 410 GE i210
 */
public class MCT410GL extends MCT400SeriesBase {

    public MCT410GL() {
        super();
    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
    }

    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
    }
}
