package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Gateway 2 is functionally equivalent to the original gateway, but may support additional functionality in the future.
 */
public class RfnGateway2 extends RfnGateway {
    
    public RfnGateway2(String name, YukonPao pao, RfnIdentifier rfnIdentifier, RfnGatewayData data) {
        super(name, pao, rfnIdentifier, data);
    }
    
}
