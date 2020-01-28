package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Wi-Fi Super Meter Virtual Gateway (VirtualGateway).
 */
public class RfnVirtualGateway extends RfnGateway {

    public RfnVirtualGateway(String name, YukonPao pao, RfnIdentifier rfnIdentifier, RfnGatewayData data) {
        super(name, pao, rfnIdentifier, data);
    }

    @Override
    public boolean isIpv6Supported() {
        return false;
    }

    @Override
    public boolean isDataStreamingSupported() {
        return true;
    }

}
