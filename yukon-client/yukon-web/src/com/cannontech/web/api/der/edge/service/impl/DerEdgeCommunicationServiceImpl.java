package com.cannontech.web.api.der.edge.service.impl;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.jms.ThriftRequestReplyTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.edgeDr.EdgeBroadcastMessagePriority;
import com.cannontech.dr.edgeDr.EdgeDrBroadcastRequest;
import com.cannontech.dr.edgeDr.EdgeDrBroadcastResponse;
import com.cannontech.dr.edgeDr.EdgeDrCommunicationException;
import com.cannontech.dr.edgeDr.EdgeUnicastPriority;
import com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr.EdgeDrBroadcastRequestSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr.EdgeDrBroadcastResponseSerializer;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.der.edge.service.DerEdgeCommunicationService;

@Service
public class DerEdgeCommunicationServiceImpl implements DerEdgeCommunicationService {
    private static final Logger log = YukonLogManager.getLogger(DerEdgeCommunicationServiceImpl.class);

    private static final int DEFAULT_TIMEOUT_MINUTES = 1;

    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private ThriftRequestReplyTemplate<EdgeDrBroadcastRequest, EdgeDrBroadcastResponse> thriftBroadcastMessenger;

    @PostConstruct
    public void initialize() {
        thriftBroadcastMessenger = new ThriftRequestReplyTemplate<EdgeDrBroadcastRequest, EdgeDrBroadcastResponse>(
                jmsTemplateFactory.createTemplate(JmsApiDirectory.EDGE_DR_BROADCAST),
                new EdgeDrBroadcastRequestSerializer(),
                new EdgeDrBroadcastResponseSerializer());
    }

    @Override
    public short sendUnicastRequest(YukonPao pao, byte[] payload, EdgeUnicastPriority queuePriority, 
            EdgeUnicastPriority networkPriority, YukonUserContext userContext) {
        
        //TODO later - clean up this logging
        log.info("Processing DER Edge Unicast Request - Pao: {}, queue priority: {}, net priority: {}, payload: {}", 
                pao, queuePriority, networkPriority, Arrays.toString(payload));
        
        //TODO later - send request to porter and receive response
        
        return (short) 0; //TODO return real e2e ID
    }

    @Override
    public void sendBroadcastRequest(byte[] payload, EdgeBroadcastMessagePriority priority, YukonUserContext userContext) {

        // TODO later - clean up this logging
        log.info("Processing DER Edge Broadcastt Request - Payload: {}, Priority: {}", Arrays.toString(payload), priority);

        final String messageGuid = UUID.randomUUID().toString();

        var requestMsg = new EdgeDrBroadcastRequest(messageGuid, payload, priority);
        var f = new CompletableFuture<EdgeDrBroadcastResponse>();
        thriftBroadcastMessenger.send(requestMsg, f);

        try {
            var responseMsg = f.get(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES);

            log.debug("Received info from Porter: {}", responseMsg);

            if (responseMsg.getError() != null) {
                throw new EdgeDrCommunicationException(responseMsg.getError().getErrorMessage());
            }

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.catching(e);
            throw new EdgeDrCommunicationException(e, "Something went wrong");
        }
    }

}
