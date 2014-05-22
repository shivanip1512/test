package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class Series5LMI extends Series5Base {

    public Series5LMI() {
        super(PaoType.SERIES_5_LMI);
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
