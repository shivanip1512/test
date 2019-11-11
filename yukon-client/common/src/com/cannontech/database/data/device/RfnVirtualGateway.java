package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

/**
 * Object representation of the WI-FI Super Meter Virtual Gateway device stored in Yukon DB. For most purposes, 
 * com.cannontech.common.rfn.model.RfnVirtualGateway will be preferable.
 */
public class RfnVirtualGateway extends RfnBase {
    
    public RfnVirtualGateway() {
        super(PaoType.VIRTUAL_GATEWAY);
    }
    
}
