package com.cannontech.common.device.programming.service.impl;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.programming.service.MeterProgramValidationService;
import com.cannontech.common.util.jms.ThriftRequestReplyTemplate;
import com.cannontech.message.porter.message.MeterProgramValidationRequest;
import com.cannontech.message.porter.message.MeterProgramValidationResponse;
import com.cannontech.messaging.serialization.thrift.serializer.porter.MeterProgramValidationRequestSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.porter.MeterProgramValidationResponseSerializer;

public class MeterProgramValidationServiceImpl implements MeterProgramValidationService {

    private static final Logger log = YukonLogManager.getLogger(MeterProgramValidationServiceImpl.class);

    private static final MeterProgramValidationRequestSerializer serializer = new MeterProgramValidationRequestSerializer();
    private static final MeterProgramValidationResponseSerializer deserializer = new MeterProgramValidationResponseSerializer();
    
    private static final int DEFAULT_TIMEOUT_MINUTES = 1;
    
    ThriftRequestReplyTemplate<MeterProgramValidationRequest, MeterProgramValidationResponse> thriftMessenger;
    
    @Autowired
    MeterProgramValidationServiceImpl(String queueName) {
        thriftMessenger = new ThriftRequestReplyTemplate<>(queueName, serializer, deserializer);
    }
    
    @Override
    public boolean isMeterProgramValid(UUID guid) throws InterruptedException, ExecutionException, TimeoutException {
        var requestMsg = new MeterProgramValidationRequest(guid);
        
        var f = new CompletableFuture<MeterProgramValidationResponse>();
        
        log.debug("Requesting info from Porter: {}", requestMsg);
        
        thriftMessenger.send(requestMsg, f);

        var responseMsg = f.get(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        
        log.debug("Received info from Porter: {}", responseMsg);
        
        return responseMsg.isValid();
    }
}
