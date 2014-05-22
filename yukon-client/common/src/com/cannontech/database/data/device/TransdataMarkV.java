package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class TransdataMarkV extends IEDMeter {

    public TransdataMarkV() {
        super(PaoType.TRANSDATA_MARKV);
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
