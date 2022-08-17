package com.cannontech.core.service.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.ThriftRequestReplyTemplate;
import com.cannontech.core.service.PorterDynamicPaoInfoService;
import com.cannontech.message.porter.message.DynamicPaoInfoDurationKeyEnum;
import com.cannontech.message.porter.message.DynamicPaoInfoPercentageKeyEnum;
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
    
    private static final int DEFAULT_TIMEOUT_MINUTES = 1;
    
    ThriftRequestReplyTemplate<DynamicPaoInfoRequest, DynamicPaoInfoResponse> thriftMessenger;
    
    @Autowired
    PorterDynamicPaoInfoServiceImpl(String queueName, ConnectionFactory connectionFactory) {
        thriftMessenger = new ThriftRequestReplyTemplate<>(connectionFactory, queueName, serializer, deserializer);
    }
    
    private DynamicPaoInfoResponse requestInfoFromPorter(DynamicPaoInfoRequest requestMsg)
            throws InterruptedException, ExecutionException, TimeoutException {
        var f = new CompletableFuture<DynamicPaoInfoResponse>();
        
        log.debug("Requesting info from Porter: {}", requestMsg);
        
        thriftMessenger.send(requestMsg, f);

        var responseMsg = f.get(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        
        log.debug("Received info from Porter: {}", responseMsg);
        
        return responseMsg;
    }

    @Override
    public VoltageProfileDetails getVoltageProfileDetails(int paoId) {
        var requestMsg = new DynamicPaoInfoRequest(paoId);
        
        requestMsg.setDurationKeys(Sets.immutableEnumSet(DynamicPaoInfoDurationKeyEnum.RFN_VOLTAGE_PROFILE_INTERVAL));
        requestMsg.setTimestampKeys(Sets.immutableEnumSet(DynamicPaoInfoTimestampKeyEnum.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL));
        
        VoltageProfileDetails details = null;
        
        try {
            DynamicPaoInfoResponse response = requestInfoFromPorter(requestMsg);
            
            details = new VoltageProfileDetails();

            Instant enabledUntil = response.getTimestampValues()
                                           .get(DynamicPaoInfoTimestampKeyEnum.RFN_VOLTAGE_PROFILE_ENABLED_UNTIL);
            Duration profileInterval = response.getDurationValues()
                                               .get(DynamicPaoInfoDurationKeyEnum.RFN_VOLTAGE_PROFILE_INTERVAL);
            details.enabledUntil = enabledUntil;
            details.profileInterval = profileInterval;
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            log.error(ex);
        }
        
        return details;
    }

    @Override
    public Duration getMctIedLoadProfileInterval(int paoId) {
        var requestMsg = new DynamicPaoInfoRequest(paoId);
        
        requestMsg.setDurationKeys(Sets.immutableEnumSet(DynamicPaoInfoDurationKeyEnum.MCT_IED_LOAD_PROFILE_INTERVAL));
        
        Duration interval = null;
        
        try {
            DynamicPaoInfoResponse response = requestInfoFromPorter(requestMsg);

            interval = response.getDurationValues()
                               .get(DynamicPaoInfoDurationKeyEnum.MCT_IED_LOAD_PROFILE_INTERVAL);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            log.error(ex);
        }
        
        return interval;
    }

    @Override
    public Double getProgrammingProgress(Integer deviceId) {
        var requestMsg = new DynamicPaoInfoRequest(deviceId);
        
        requestMsg.setPercentageKeys(Sets.immutableEnumSet(DynamicPaoInfoPercentageKeyEnum.METER_PROGRAMMING_PROGRESS));
        
        Double progress = null;
        
        try {
            DynamicPaoInfoResponse response = requestInfoFromPorter(requestMsg);

            progress = response.getPercentageValues()
                               .get(DynamicPaoInfoPercentageKeyEnum.METER_PROGRAMMING_PROGRESS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            log.error(ex);
        }
        
        return progress;
    }
}
