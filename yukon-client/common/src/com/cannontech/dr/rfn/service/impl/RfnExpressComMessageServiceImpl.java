package com.cannontech.dr.rfn.service.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.jms.JmsReplyReplyHandler;
import com.cannontech.common.util.jms.RequestReplyReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.dr.rfn.message.broadcast.RfnExpressComBroadcastReply;
import com.cannontech.dr.rfn.message.broadcast.RfnExpressComBroadcastRequest;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReply;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReply;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastRequest;
import com.cannontech.dr.rfn.service.RfnBroadcastCompletionCallback;
import com.cannontech.dr.rfn.service.RfnUnicastCompletionCallback;
import com.cannontech.dr.rfn.service.RfnExpressComMessageService;
import com.google.common.collect.Lists;

public class RfnExpressComMessageServiceImpl implements RfnExpressComMessageService {
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private ConnectionFactory connectionFactory;
    
    private RequestReplyReplyTemplate<RfnExpressComUnicastReply, RfnExpressComUnicastDataReply> unicastTemplate;
    private RequestReplyReplyTemplate<RfnExpressComUnicastReply, RfnExpressComUnicastDataReply> unicastBulkTemplate;
    private RequestReplyTemplate<RfnExpressComBroadcastReply> broadcastTemplate;
    
    @Override
    public void sendUnicast(final RfnExpressComUnicastRequest request, final RfnUnicastCompletionCallback callback) {
        JmsReplyReplyHandler<RfnExpressComUnicastReply, RfnExpressComUnicastDataReply> handler = new JmsReplyReplyHandler<RfnExpressComUnicastReply, RfnExpressComUnicastDataReply>() {

            @Override
            public void complete() {
                callback.complete();
            }

            @Override
            public Class<RfnExpressComUnicastReply> getExpectedType1() {
                return RfnExpressComUnicastReply.class;
            }

            @Override
            public Class<RfnExpressComUnicastDataReply> getExpectedType2() {
                return RfnExpressComUnicastDataReply.class;
            }

            @Override
            public void handleException(Exception e) {
                callback.processingExceptionOccured(e.getMessage());
            }

            @Override
            public boolean handleReply1(RfnExpressComUnicastReply statusReply) {
                if (!statusReply.isSuccess()) {
                    /* Request failed */
                    callback.receivedStatusError(statusReply.getReplyType());
                    return false;
                } else {
                    /* Request successful */
                    callback.receivedStatus(statusReply.getReplyType());
                    return true;
                }
            }

            @Override
            public void handleReply2(RfnExpressComUnicastDataReply dataReplyMessage) {
                if (!dataReplyMessage.isSuccess()) {
                    /* Data response failed */
                    callback.receivedDataError(dataReplyMessage.getReplyType());
                } else {
                    /* Data response successful, process point data */
                    
                    List<Object> resultObjects = Lists.newArrayList();
                    //TODO Garrett
                    // resultObjects = garrettService.garrett(dataReplyMessage);

                    for (Object resultObject : resultObjects) {
                        callback.receivedData(resultObject);
                    }
                }
           }

            @Override
            public void handleTimeout1() {
                callback.receivedStatusError(RfnExpressComUnicastReplyType.TIMEOUT);
            }

            @Override
            public void handleTimeout2() {
                callback.receivedDataError(RfnExpressComUnicastDataReplyType.TIMEOUT);
            }
        };
        
        unicastTemplate.send(request, handler);
    }
    
    @Override
    public void sendUnicastBulk(final Collection<RfnExpressComUnicastRequest> device, final RfnUnicastCompletionCallback callback) {
        // TODO
    }
    
    @Override
    public void sendBroadcast(final Collection<RfnExpressComBroadcastRequest> device, final RfnBroadcastCompletionCallback callback) {
        // TODO
    }

    @PostConstruct
    public void initialize() {
        broadcastTemplate = new RequestReplyTemplate<RfnExpressComBroadcastReply>();
        broadcastTemplate.setConfigurationName("RFN_XCOMM_REQUEST");
        broadcastTemplate.setConfigurationSource(configurationSource);
        broadcastTemplate.setConnectionFactory(connectionFactory);
        broadcastTemplate.setRequestQueueName("yukon.qr.obj.dr.rfn.ExpressComBroadcastRequest", false);
        
        unicastTemplate = new RequestReplyReplyTemplate<RfnExpressComUnicastReply, RfnExpressComUnicastDataReply>();
        unicastTemplate.setConfigurationName("RFN_XCOMM_REQUEST");
        unicastTemplate.setConfigurationSource(configurationSource);
        unicastTemplate.setConnectionFactory(connectionFactory);
        unicastTemplate.setRequestQueueName("yukon.qr.obj.dr.rfn.ExpressComUnicastRequest", false);
        
        unicastBulkTemplate = new RequestReplyReplyTemplate<RfnExpressComUnicastReply, RfnExpressComUnicastDataReply>();
        unicastBulkTemplate.setConfigurationName("RFN_XCOMM_REQUEST");
        unicastBulkTemplate.setConfigurationSource(configurationSource);
        unicastBulkTemplate.setConnectionFactory(connectionFactory);
        unicastBulkTemplate.setRequestQueueName("yukon.qr.obj.dr.rfn.ExpressComBulkUnicastRequest", false);
    }
    
}