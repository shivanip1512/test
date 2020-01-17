package com.cannontech.database.data.device;

import java.sql.SQLException;

import com.cannontech.common.pao.PaoType;

public abstract class RfnGatewayBase extends RfnBase {
    
    public RfnGatewayBase(PaoType paoType) {
        super(paoType);
    }
    
    @Override
    public void delete() throws SQLException {
        delete("DynamicRfnDeviceData", "GatewayId", getPAObjectID());
        super.delete();
    }
}