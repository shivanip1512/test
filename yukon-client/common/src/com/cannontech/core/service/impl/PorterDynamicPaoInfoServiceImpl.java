package com.cannontech.core.service.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.ThriftRequestReplyTemplate;
import com.cannontech.core.service.PorterDynamicPaoInfoService;
import com.cannontech.message.porter.message.DynamicPaoInfoDurationKeyEnum;
import com.cannontech.message.porter.message.DynamicPaoInfoRequest;
import com.cannontech.message.porter.message.DynamicPaoInfoResponse;
import com.cannontech.message.porter.message.DynamicPaoInfoTimestampKeyEnum;
import com.cannontech.messaging.serialization.thrift.serializer.porter.DynamicPaoInfoRequestSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.porter.DynamicPaoInfoResponseSerializer;
import com.google.common.collect.Sets;

public class PorterDynamicPaoInfoServiceImpl implements PorterDynamicPaoInfoService {

    private static final Logger log = YukonLogManager.getLogger(PorterDynamicPaoInfoServiceImpl.class);

    private static final DynamicPaoInfoRequestSerializer serializer = new DynamicPaoInfoRequestSerializer();
    private static final DynamicPaoInfoResponseSerializer deserializer = new DynamicPaoInfoResponseSerializer();
    
    ThriftRequestReplyTemplate<DynamicPaoInfoRequest, DynamicPaoInfoResponse> thriftMessenger;
    
    @Autowired
    PorterDynamicPaoInfoServiceImpl(String queueName, ConnectionFactory connectionFactory) {
        thriftMessenger = new ThriftRequestReplyTemplate<DynamicPaoInfoRequest, DynamicPaoInfoResponse>(
                 connectionFactory, queueName, serializer, deserializer);
    }
    
    @Override
    public void getVoltageProfileDetails(int paoId, Consumer<VoltageProfileDetails> callback) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                callback.accept(getVoltageProfileDetails(paoId));
            }
        });
    }

    @Override
    public VoltageProfileDetails getVoltageProfileDetails(int paoId) {
        DynamicPaoInfoRequest requestMsg = new DynamicPaoInfoRequest();
        
        requestMsg.setDeviceID(paoId);
        requestMsg.setDurationKeys(Sets.immutableEnumSet(DynamicPaoInfoDurationKeyEnum.RFN_VOLTAGE_PROFILE_INTERVAL));
        requestMsg.setTimestampKeys(Sets.immutableEnumSet(DynamicPaoInfoTimestampKeyEnum.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL));
        
        CompletableFuture<DynamicPaoInfoResponse> f = new CompletableFuture<DynamicPaoInfoResponse>();
        
        thriftMessenger.send(requestMsg, f);
        
        VoltageProfileDetails details = null;
        
        try {
            DynamicPaoInfoResponse response = f.get(1, TimeUnit.MINUTES);
            
            Instant enabledUntil = response.getTimestampValues().get(DynamicPaoInfoTimestampKeyEnum.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL);
            Duration profileInterval = response.getDurationValues().get(DynamicPaoInfoDurationKeyEnum.RFN_VOLTAGE_PROFILE_INTERVAL);
            
            details = new VoltageProfileDetails();
            
            details.enabledUntil = enabledUntil;
            details.profileInterval = profileInterval;
        }
        catch( InterruptedException | ExecutionException | TimeoutException ex ) {
            log.error(ex);
        }
        
        return details;
    }
}
