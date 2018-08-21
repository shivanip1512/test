package com.cannontech.cbc.commands;

import com.cannontech.message.capcontrol.model.CapControlServerResponse;

public interface CommandResultCallback {
    public void processingExceptionOccurred(String reason);
    public CapControlServerResponse getResponse();
    public void receivedResponse(CapControlServerResponse message);
    public String getErrorMessage();
}