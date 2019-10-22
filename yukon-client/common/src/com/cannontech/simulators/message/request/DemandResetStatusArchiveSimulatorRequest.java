package com.cannontech.simulators.message.request;

import com.cannontech.amr.rfn.message.status.type.DemandResetStatusCode;
import com.cannontech.simulators.SimulatorType;

/**
 * Request to send a demand reset.
 */
public class DemandResetStatusArchiveSimulatorRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    
    private int messageCount;
    private DemandResetStatusCode code;
    
    public DemandResetStatusArchiveSimulatorRequest(int messageCount, DemandResetStatusCode code) {
        this.messageCount = messageCount;
        this.code = code;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.STATUS_ARCHIVE;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public DemandResetStatusCode getDemandResetStatusCode() {
        return code;
    }
}
