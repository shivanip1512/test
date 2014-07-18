package com.cannontech.cbc.commands;

import com.cannontech.message.capcontrol.model.CapControlServerResponse;

public interface CommandResultCallback {
    public void processingExceptionOccured(String reason);
    public CapControlServerResponse getResponse();
    public void recievedResponse(CapControlServerResponse message);
    public String getErrorMessage();
}