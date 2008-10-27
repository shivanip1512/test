package com.cannontech.loadcontrol.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.messages.LMManualControlResponse;
import com.cannontech.loadcontrol.service.LoadControlCommandService;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.ServerRequest;

public class LoadControlCommandServiceImpl implements LoadControlCommandService {

    private LoadControlClientConnection loadControlClientConnection;
    private ServerRequest serverRequest;
    
    @SuppressWarnings("unchecked")
    public List<String> executeManualCommand(LMManualControlRequest request) {
        
        ServerResponseMsg response = serverRequest.makeServerRequest(loadControlClientConnection, request); 
        
        LMManualControlResponse lmResponse = (LMManualControlResponse)response.getPayload();
        
        return lmResponse.getConstraintViolations();
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
