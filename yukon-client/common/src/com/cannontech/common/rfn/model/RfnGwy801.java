package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;

// Do we really need to extend from RfnGwy800 ??
public class RfnGwy801 extends RfnGwy800 {

    public RfnGwy801(String name, YukonPao pao, RfnIdentifier rfnIdentifier, RfnGatewayData data) {
        super(name, pao, rfnIdentifier, data);
    }
}
