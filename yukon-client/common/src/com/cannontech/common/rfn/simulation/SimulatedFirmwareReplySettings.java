package com.cannontech.common.rfn.simulation;

import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;

public class SimulatedFirmwareReplySettings {
    GatewayFirmwareUpdateRequestResult resultType = GatewayFirmwareUpdateRequestResult.ACCEPTED;

    public GatewayFirmwareUpdateRequestResult getResultType() {
        return resultType;
    }

    public void setResultType(GatewayFirmwareUpdateRequestResult resultType) {
        this.resultType = resultType;
    }
    
}
