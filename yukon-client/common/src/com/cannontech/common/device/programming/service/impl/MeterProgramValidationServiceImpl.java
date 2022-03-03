package com.cannontech.common.device.programming.service.impl;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.programming.service.MeterProgramValidationService;
import com.cannontech.common.exception.ServiceCommunicationFailedException;
import com.cannontech.common.util.jms.ThriftRequestReplyTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.message.porter.message.MeterProgramValidationRequest;
import com.cannontech.message.porter.message.MeterProgramValidationResponse;
import com.cannontech.messaging.serialization.thrift.serializer.porter.MeterProgramValidationRequestSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.porter.MeterProgramValidationResponseSerializer;

public class MeterProgramValidationServiceImpl implements MeterProgramValidationService {

    private static final Logger log = YukonLogManager.getLogger(MeterProgramValidationServiceImpl.class);

    private static final int DEFAULT_TIMEOUT_MINUTES = 1;
    
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private ThriftRequestReplyTemplate<MeterProgramValidationRequest, MeterProgramValidationResponse> thriftMessenger;
    
    @PostConstruct
    public void initialize() {
        thriftMessenger = new ThriftRequestReplyTemplate<>(
                jmsTemplateFactory.createTemplate(JmsApiDirectory.METER_PROGRAM_VALIDATION),
                new MeterProgramValidationRequestSerializer(),
                new MeterProgramValidationResponseSerializer());
    }
    
    @Override
    public boolean isMeterProgramValid(UUID guid) throws ServiceCommunicationFailedException {
        var requestMsg = new MeterProgramValidationRequest(guid);
        
        var f = new CompletableFuture<MeterProgramValidationResponse>();
        
        log.debug("Requesting info from Porter: {}", requestMsg);
        
        thriftMessenger.send(requestMsg, f);

        try {
            var responseMsg = f.get(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES);

            log.debug("Received info from Porter: {}", responseMsg);
            
            return responseMsg.isValid();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.catching(e);
            
            throw new ServiceCommunicationFailedException(e);
        }
    }
}
