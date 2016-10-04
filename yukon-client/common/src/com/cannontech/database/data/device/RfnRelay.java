package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

/**
 * Object representation of the limited relay device stored in Yukon DB. 
 */
public class RfnRelay extends RfnBase {
    
    public RfnRelay() {
        super(PaoType.RFN_RELAY);
    }
}
