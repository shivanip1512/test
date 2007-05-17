package com.cannontech.database.data.device;

import java.sql.Connection;

/**
 * 430 Sentinal
 */
public class MCT430SN extends MCT400SeriesBase {

    public MCT430SN() {
        super();
    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
    }

    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
    }
}