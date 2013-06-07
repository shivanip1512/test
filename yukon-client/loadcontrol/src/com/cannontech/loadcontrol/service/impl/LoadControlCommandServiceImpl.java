package com.cannontech.loadcontrol.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.service.LoadControlCommandService;
import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;
import com.cannontech.messaging.message.loadcontrol.ManualControlResponseMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.messaging.util.BadServerResponseException;
import com.cannontech.messaging.util.ServerRequest;

public class LoadControlCommandServiceImpl implements LoadControlCommandService {

    @Autowired private LoadControlClientConnection loadControlClientConnection;
    @Autowired private ServerRequest serverRequest;
    
    public ConstraintViolations executeManualCommand(ManualControlRequestMessage request) throws BadServerResponseException {
        
        ServerResponseMessage response = serverRequest.makeServerRequest(loadControlClientConnection, request); 
        
        ManualControlResponseMessage lmResponse = (ManualControlResponseMessage)response.getPayload();
        
        if (response.getStatus() != ServerResponseMessage.STATUS_OK) {
            throw new BadServerResponseException("Invalid response received from Load Management. Response status:" + response.getStatus());
        }
        return new ConstraintViolations(LCUtils.convertViolationsToContainers(lmResponse.getConstraintViolations()));
    }
}