package com.cannontech.web.dev;

import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.simulators.SimulatorUtils;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;

/**
 * Sends requests to and receives responess from the Yukon simulator service.
 */
public class SimulatorsCommunicationService {
    private static final Logger log = YukonLogManager.getLogger(SimulatorsCommunicationService.class);
    private static final String simulatorRequestCparm = "SIMULATOR_REQUEST";
    @Autowired private ConfigurationSource configSource;
    @Autowired private ConnectionFactory connectionFactory;
    
    public static final String COMMUNICATION_ERROR_KEY = "yukon.web.modules.dev.rfnTest.gatewaySimulator.simulatorCommunicationError";
    
    
    // OPTION #1 - Build a new template each time. This allows us to specify the generic type, but building
    // the template might be expensive?
    /*public <T extends SimulatorResponse> T sendRequest(SimulatorRequest request, Class<T> responseClass) throws ExecutionException {
        // Create template
        RequestReplyTemplate<T> requestTemplate = new RequestReplyTemplateImpl<>(simulatorRequestCparm, 
                configSource, connectionFactory, simulatorsQueue, false);
        
        // Send request
        log.trace("Sending request: " + request);
        BlockingJmsReplyHandler<T> replyHandler = new BlockingJmsReplyHandler<>(responseClass);
        requestTemplate.send(request, replyHandler);
        
        // Wait for response
        T response = replyHandler.waitForCompletion();
        log.debug("Received response: " + response);
        
        return response;
    }*/
    
    // OPTION #2 - Use request & response interfaces. This requires the caller to cast the response to the
    // appropriate type.
    // (This would use:  private RequestReplyTemplate<SimulatorResponse> requestTemplate;)
    /*public SimulatorResponse sendRequest(SimulatorRequest request) throws ExecutionException {
        // Send request
        log.trace("Sending request: " + request);
        BlockingJmsReplyHandler<SimulatorResponse> replyHandler = 
                new BlockingJmsReplyHandler<>(SimulatorResponse.class);
        requestTemplate.send(request, replyHandler);
        
        // Wait for response
        SimulatorResponse response = replyHandler.waitForCompletion();
        log.debug("Received response: " + response);
        return response;
    }*/
    
    @SuppressWarnings("rawtypes")
    private RequestReplyTemplate requestTemplate;
    
    @PostConstruct
    public void init() {
        requestTemplate = new RequestReplyTemplateImpl<>(simulatorRequestCparm, 
                configSource, connectionFactory, SimulatorUtils.SIMULATORS_REQUEST_QUEUE, false);
    }
    
    //OPTION #3 - RequestReplyTemplate is a raw type, but the method signature uses generics
    //Similar to #2, but with a warning instead of casts.
    @SuppressWarnings("unchecked")
    public <T extends SimulatorResponse> T sendRequest(SimulatorRequest request, Class<T> responseClass) throws ExecutionException {
        // Send request
        log.trace("Sending request: " + request);
        BlockingJmsReplyHandler<T> replyHandler = new BlockingJmsReplyHandler<>(responseClass);
        requestTemplate.send(request, replyHandler);
        
        // Wait for response
        T response = replyHandler.waitForCompletion();
        log.debug("Received response: " + response);

        return response;
    }
}
