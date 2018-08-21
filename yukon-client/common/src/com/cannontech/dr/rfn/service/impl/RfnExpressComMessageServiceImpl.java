package com.cannontech.dr.rfn.service.impl;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.service.RfnDeviceReadCompletionCallback;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.message.RfnMessageClass;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.jms.JmsReplyHandler;
import com.cannontech.common.util.jms.JmsReplyReplyHandler;
import com.cannontech.common.util.jms.RequestReplyReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.dr.rfn.message.broadcast.RfnExpressComBroadcastRequest;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReply;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReply;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastRequest;
import com.cannontech.dr.rfn.service.RawExpressComCommandBuilder;
import com.cannontech.dr.rfn.service.RfnExpressComMessageService;
import com.cannontech.dr.rfn.service.RfnUnicastCallback;
import com.cannontech.dr.rfn.service.RfnUnicastDataCallback;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.google.common.collect.Sets;

public class RfnExpressComMessageServiceImpl implements RfnExpressComMessageService {
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private RawExpressComCommandBuilder commandBuilder;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    private final static Logger log = YukonLogManager.getLogger(RfnExpressComMessageServiceImpl.class);
    
    private JmsTemplate jmsTemplate;
    private RequestReplyReplyTemplate<RfnExpressComUnicastReply, RfnExpressComUnicastDataReply> unicastWithDataTemplate;
    private RequestReplyTemplateImpl<RfnExpressComUnicastReply> unicastTemplate;
    private Random random = new Random(System.currentTimeMillis());
    
    @Override
    public void sendUnicastRequest(final RfnExpressComUnicastRequest request, final RfnUnicastCallback callback) {
        JmsReplyHandler<RfnExpressComUnicastReply> handler = new JmsReplyHandler<RfnExpressComUnicastReply>() {
            
            @Override
            public void complete() {
                callback.complete();
            }

            @Override
            public void handleException(Exception e) {
                log.error(request.getRfnIdentifier() + " - unicast request failed", e);
                MessageSourceResolvable message = 
                        YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.rfnExpressComMessage.error", e.toString());
                callback.processingExceptionOccurred(message);
            }

            @Override
            public void handleTimeout() {
                log.info(request.getRfnIdentifier() + " - unicast request timed out.");
                callback.receivedStatusError(RfnExpressComUnicastReplyType.TIMEOUT);
            }

            @Override
            public void handleReply(RfnExpressComUnicastReply t) {
                log.info(request.getRfnIdentifier() + " - received reply(" + t.getReplyType() + ") from NM ");
                callback.receivedStatus(t.getReplyType());
            }

            @Override
            public Class<RfnExpressComUnicastReply> getExpectedType() {
                return RfnExpressComUnicastReply.class;
            }
        };
        request.setMessageId(nextMessageId());
        unicastTemplate.send(request, handler);
    }
    
    @Override
    public void sendUnicastDataRequest(final RfnExpressComUnicastRequest request, final RfnUnicastDataCallback callback) {
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
                log.error(request.getRfnIdentifier() + " - unicast request failed", e);
                MessageSourceResolvable message = 
                        YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.rfnExpressComMessage.error", e.toString());
                callback.processingExceptionOccurred(message);
            }

            @Override
            public boolean handleReply1(RfnExpressComUnicastReply statusReply) {
                log.info(request.getRfnIdentifier() + " - received reply(" + statusReply.getReplyType() + ") from NM ");
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
                log.info(request.getRfnIdentifier() + " - received reply(" + dataReplyMessage.getReplyType() + ") from NM ");
                if (!dataReplyMessage.isSuccess()) {
                    /* Data response failed */
                    callback.receivedDataError(dataReplyMessage.getReplyType());
                } else {
                    /* Data response successful, process point data */
                    
                    //WARNING
                    //As of Oct. 15, 2013, Network Manager does not send a data reply for RFN expresscom.
                    //Do not rely on a response to confirm message was sent successfully.
                    //Instead, listen for the expected point updates. See 
                    //DeviceAttributeReadRfnStrategy.DataListeningReadCompletionCallback
                }
           }

            @Override
            public void handleTimeout1() {
                log.info(request.getRfnIdentifier() + " - unicast request timed out.");
                callback.receivedStatusError(RfnExpressComUnicastReplyType.TIMEOUT);
            }

            @Override
            public void handleTimeout2() {
                log.info(request.getRfnIdentifier() + " - unicast request timed out.");
                callback.receivedDataError(RfnExpressComUnicastDataReplyType.TIMEOUT);
            }
        };
        
        request.setMessageId(nextMessageId());
        unicastWithDataTemplate.send(request, handler);
    }
    
    @Override
    public Set<String> sendUnicastBulkRequest(final Collection<RfnExpressComUnicastRequest> requests) {
        Set<String> messageIds = Sets.newHashSet();
        for (RfnExpressComUnicastRequest request : requests) {
            // We will probably need to keep track of the responses at some point.
            String messageId = nextMessageId();
            request.setMessageId(messageId);
            jmsTemplate.convertAndSend("yukon.qr.obj.dr.rfn.ExpressComBulkUnicastRequest", request);
            messageIds.add(messageId);
        }
        
        return messageIds;
    }
    
    @PostConstruct
    public void initialize() {
        unicastWithDataTemplate = new RequestReplyReplyTemplate<>(
                "RFN_XCOMM_REQUEST", configurationSource, connectionFactory, 
                "yukon.qr.obj.dr.rfn.ExpressComUnicastRequest", false);
        
        unicastTemplate = new RequestReplyTemplateImpl<>(
                "RFN_XCOMM_REQUEST", configurationSource, connectionFactory, 
                "yukon.qr.obj.dr.rfn.ExpressComUnicastRequest", false);
    }
    
    /**
     * Generates unique message id's for rf expresscom unicast requests.
     */
    private String nextMessageId() {
        return Long.toHexString(random.nextLong());
    }

    @Override
    public void readDevice(RfnDevice device, final RfnUnicastCallback callback) {
        LiteLmHardwareBase lmhb = inventoryBaseDao.getHardwareByDeviceId(device.getPaoIdentifier().getPaoId());
        LmHardwareCommand lmhc = new LmHardwareCommand();
        lmhc.setDevice(lmhb);
        lmhc.setType(LmHardwareCommandType.READ_NOW);
        lmhc.setUser(null);

        RfnExpressComUnicastRequest request = new RfnExpressComUnicastRequest(device.getRfnIdentifier());
        request.setMessageId(nextMessageId());
        request.setMessagePriority(6);
        request.setPayload(commandBuilder.getCommandAsHexStringByteArray(lmhc));
        request.setResponseExpected(false);
        request.setRfnMessageClass(RfnMessageClass.DR);
        
        sendUnicastRequest(request, callback);
    }
    
    @Override
    public void readDevice(RfnDevice device, final RfnDeviceReadCompletionCallback<RfnExpressComUnicastReplyType, RfnExpressComUnicastDataReplyType> delegateCallback) {
        RfnExpressComUnicastRequest request = new RfnExpressComUnicastRequest(device.getRfnIdentifier());
        request.setMessageId(nextMessageId());
        request.setMessagePriority(6);
        LiteLmHardwareBase lmhb = inventoryBaseDao.getHardwareByDeviceId(device.getPaoIdentifier().getPaoId());
        // The rf command strategy does not actually need the user, so null for now.
        LmHardwareCommand lmhc = new LmHardwareCommand();
        lmhc.setDevice(lmhb);
        lmhc.setType(LmHardwareCommandType.READ_NOW);
        lmhc.setUser(null);
        
        request.setPayload(commandBuilder.getCommandAsHexStringByteArray(lmhc));
        request.setResponseExpected(true);
        request.setRfnMessageClass(RfnMessageClass.DR);
        
        sendUnicastDataRequest(request, new RfnUnicastDataCallback() {
            @Override public void processingExceptionOccurred(MessageSourceResolvable message) {
                delegateCallback.processingExceptionOccurred(message);
            }
            @Override public void complete() {
                delegateCallback.complete();
            }
            @Override public void receivedStatusError(RfnExpressComUnicastReplyType replyType) {
                delegateCallback.receivedStatusError(replyType);
            }
            @Override public void receivedStatus(RfnExpressComUnicastReplyType replyType) {
                delegateCallback.receivedStatus(replyType);
            }
            @Override public void receivedDataError(RfnExpressComUnicastDataReplyType replyType) {
                delegateCallback.receivedDataError(replyType);
            }
            @Override public void receivedData(Object data) {
                try {
                    PointValueHolder pointValue = (PointValueHolder)data;
                    delegateCallback.receivedData(pointValue);
                } catch (ClassCastException e) {
                    // We won't care about the non-point data for now.
                }
            }
        });
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }

    @Override
    public void sendBroadcastRequest(RfnExpressComBroadcastRequest request) {
        jmsTemplate.convertAndSend("yukon.qr.obj.dr.rfn.ExpressComBroadcastRequest", request);
    }
    
}