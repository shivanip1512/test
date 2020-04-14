package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

/**
 * Object representation of the limited gateway device stored in Yukon DB. For most purposes, 
 * com.cannontech.common.rfn.model.RfnGateway will be preferable.
 */
public class RfnGateway extends RfnGatewayBase {
    
    public RfnGateway() {
        super(PaoType.RFN_GATEWAY);
    }
}
