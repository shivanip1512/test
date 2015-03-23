package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

/**
 * Object representation of the limited gateway 2.0 device stored in Yukon DB. For most purposes, 
 * com.cannontech.common.rfn.model.RfnGateway2 will be preferable.
 */
public class RfnGateway2 extends RfnBase {
    
    public RfnGateway2() {
        super(PaoType.RFN_GATEWAY_2);
    }
    
}
