package com.cannontech.cbc.commands;

import com.cannontech.messaging.message.capcontrol.ServerResponseMessage;

public interface CommandResultCallback {
    public void processingExceptionOccured(String reason);
    public ServerResponseMessage getResponse();
    public void recievedResponse(ServerResponseMessage message);
    public String getErrorMessage();
}