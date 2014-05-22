package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class KV extends IEDMeter {

    /**
     * Valid paoTypes are kV and kvII
     * @param paoType
     */
    public KV(PaoType paoType) {
        super(paoType);
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
