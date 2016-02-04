package com.cannontech.common.rfn.simulation;

import java.io.Serializable;

import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;

public class SimulatedFirmwareReplySettings implements Serializable {
    private static final long serialVersionUID = 1L;
    private GatewayFirmwareUpdateRequestResult resultType = GatewayFirmwareUpdateRequestResult.ACCEPTED;

    public GatewayFirmwareUpdateRequestResult getResultType() {
        return resultType;
    }

    public void setResultType(GatewayFirmwareUpdateRequestResult resultType) {
        this.resultType = resultType;
    }
    
}
