package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

/**
 * Request to create meters.
 */
public class StatusArchiveSimulatorRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    
    private int messageCount;
    
    public StatusArchiveSimulatorRequest(int messageCount) {
        this.messageCount = messageCount;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.STATUS_ARCHIVE;
    }

    public int getMessageCount() {
        return messageCount;
    }
}
