package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class DCT_501 extends MCTBase {
    /**
     * DCT constructor comment.
     */
    public DCT_501() {
        super(PaoType.DCT_501);
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
