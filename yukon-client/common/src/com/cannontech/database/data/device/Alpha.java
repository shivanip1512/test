package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class Alpha extends IEDMeter {

    /**
     * Valid paoTypes are Alpha A1 and Alpha Plus
     * @param paoType
     */
    public Alpha(PaoType paoType) {
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
