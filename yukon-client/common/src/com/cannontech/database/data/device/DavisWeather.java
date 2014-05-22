package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class DavisWeather extends IEDMeter {

    public DavisWeather() {
        super(PaoType.DAVISWEATHER);
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
