package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;

public class RfnGwy801 extends RfnGateway {

    public RfnGwy801(String name, YukonPao pao, RfnIdentifier rfnIdentifier, RfnGatewayData data) {
        super(name, pao, rfnIdentifier, data);
    }

    @Override
    public boolean isDataStreamingSupported() {
        return true;
    }
}
