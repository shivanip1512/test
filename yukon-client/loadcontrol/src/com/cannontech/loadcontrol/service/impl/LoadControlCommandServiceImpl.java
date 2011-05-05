package com.cannontech.loadcontrol.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.messages.LMManualControlResponse;
import com.cannontech.loadcontrol.service.LoadControlCommandService;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ServerRequest;

public class LoadControlCommandServiceImpl implements LoadControlCommandService {

    private LoadControlClientConnection loadControlClientConnection;
    private ServerRequest serverRequest;
    
    public ConstraintViolations executeManualCommand(LMManualControlRequest request) throws BadServerResponseException {
        
        ServerResponseMsg response = serverRequest.makeServerRequest(loadControlClientConnection, request); 
        
        LMManualControlResponse lmResponse = (LMManualControlResponse)response.getPayload();
        
        if (response.getStatus() != ServerResponseMsg.STATUS_OK) {
            throw new BadServerResponseException();
        }
        return new ConstraintViolations(LCUtils.convertViolationsToContainers(lmResponse.getConstraintViolations()));
    }

    @Autowired
    public void setLoadControlClientConnection(
            LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }
    
    @Autowired
    public void setServerRequest(ServerRequest serverRequest) {
        this.serverRequest = serverRequest;
    }
}