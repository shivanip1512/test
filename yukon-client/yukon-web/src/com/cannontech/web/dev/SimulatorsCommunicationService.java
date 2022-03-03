package com.cannontech.web.dev;

import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;

/**
 * Sends requests to and receives responses from the Yukon simulator service.
 */
public class SimulatorsCommunicationService {
    private static final Logger log = YukonLogManager.getLogger(SimulatorsCommunicationService.class);
    private static final String simulatorRequestCparm = "SIMULATOR_REQUEST";
    @Autowired private ConfigurationSource configSource;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    @SuppressWarnings("rawtypes") private RequestReplyTemplate requestTemplate;
    
    public static final String COMMUNICATION_ERROR_KEY = "yukon.web.modules.dev.rfnTest.gatewaySimulator.simulatorCommunicationError";
    
    @PostConstruct
    public void init() {
        YukonJmsTemplate jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.SIMULATORS);
        requestTemplate = new RequestReplyTemplateImpl<>(simulatorRequestCparm, configSource, jmsTemplate, true);
    }
    
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
