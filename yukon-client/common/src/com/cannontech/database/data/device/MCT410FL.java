package com.cannontech.database.data.device;

import java.sql.Connection;

/**
 * 410 Focus
 */
public class MCT410FL extends MCT400SeriesBase {

    public MCT410FL() {
        super();
    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
    }

    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
    }
}
