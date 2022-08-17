package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Gateway 2.0 (GWY-800) is functionally equivalent to the original gateway, but may support additional functionality in the future.
 */
public class RfnGwy800 extends RfnGateway {
    
    public RfnGwy800(String name, YukonPao pao, RfnIdentifier rfnIdentifier, RfnGatewayData data) {
        super(name, pao, rfnIdentifier, data);
    }

    @Override
    public boolean isDataStreamingSupported() {
        return true;
    }

}
