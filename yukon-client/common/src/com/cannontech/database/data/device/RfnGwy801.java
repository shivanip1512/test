package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

/**
 * Object representation of the limited gateway 2.0 device stored in Yukon DB. For most purposes, 
 * com.cannontech.common.rfn.model.RfnGwy801 will be preferable.
 * 
 */
public class RfnGwy801 extends RfnGatewayBase {
    
    public RfnGwy801() {
        super(PaoType.GWY801);
    }
    
}
