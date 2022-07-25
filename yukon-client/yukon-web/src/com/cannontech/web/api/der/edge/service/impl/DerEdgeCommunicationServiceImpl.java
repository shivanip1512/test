package com.cannontech.web.api.der.edge.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.jms.ThriftRequestReplyTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.edgeDr.EdgeBroadcastMessagePriority;
import com.cannontech.dr.edgeDr.EdgeDrBroadcastRequest;
import com.cannontech.dr.edgeDr.EdgeDrBroadcastResponse;
import com.cannontech.dr.edgeDr.EdgeDrCommunicationException;
import com.cannontech.dr.edgeDr.EdgeDrUnicastRequest;
import com.cannontech.dr.edgeDr.EdgeDrUnicastResponse;
import com.cannontech.dr.edgeDr.EdgeUnicastPriority;
import com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr.EdgeDrBroadcastRequestSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr.EdgeDrBroadcastResponseSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr.EdgeDrUnicastRequestSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr.EdgeDrUnicastResponseSerializer;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.der.edge.service.DerEdgeCommunicationService;
import com.cannontech.web.api.der.edge.service.DerEdgeResponseService;

@Service
public class DerEdgeCommunicationServiceImpl implements DerEdgeCommunicationService {
    private static final Logger log = YukonLogManager.getLogger(DerEdgeCommunicationServiceImpl.class);

    private static final int DEFAULT_TIMEOUT_MINUTES = 1;

    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private DerEdgeResponseService derEdgeResponseService;

    private ThriftRequestReplyTemplate<EdgeDrBroadcastRequest, EdgeDrBroadcastResponse> thriftBroadcastMessenger;
    private ThriftRequestReplyTemplate<EdgeDrUnicastRequest, EdgeDrUnicastResponse> thriftUnicastMessenger;

    @PostConstruct
    public void initialize() {
        thriftBroadcastMessenger = new ThriftRequestReplyTemplate<>(
                jmsTemplateFactory.createTemplate(JmsApiDirectory.EDGE_DR_BROADCAST),
                new EdgeDrBroadcastRequestSerializer(),
                new EdgeDrBroadcastResponseSerializer(),
                JmsApiDirectory.EDGE_DR_BROADCAST.getResponseQueueName());

        thriftUnicastMessenger = new ThriftRequestReplyTemplate<>(
                jmsTemplateFactory.createTemplate(JmsApiDirectory.EDGE_DR_UNICAST), 
                new EdgeDrUnicastRequestSerializer(),
                new EdgeDrUnicastResponseSerializer(),
                JmsApiDirectory.EDGE_DR_UNICAST.getResponseQueueName());
    }

    @Override
    public String sendUnicastRequest(YukonPao pao, byte[] payload, EdgeUnicastPriority queuePriority, 
            EdgeUnicastPriority networkPriority, YukonUserContext userContext) throws EdgeDrCommunicationException {
        
        //TODO later - clean up this logging
        log.info("Processing DER Edge Unicast Request - Pao: {}, queue priority: {}, net priority: {}, payload: {}", 
                pao, queuePriority, networkPriority, Arrays.toString(payload));

        final String messageGuid = UUID.randomUUID().toString();

        var requestMsg = new EdgeDrUnicastRequest(pao.getPaoIdentifier().getPaoId(), messageGuid, payload, queuePriority, networkPriority);
        var completableFuture = new CompletableFuture<EdgeDrUnicastResponse>();
        thriftUnicastMessenger.send(requestMsg, completableFuture);

        try {
            var responseMsg = completableFuture.get(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES);

            log.debug("Received info from Porter: {}", responseMsg);

            if (responseMsg == null) {
                throw new EdgeDrCommunicationException("Response time out.");
            }
            else if (responseMsg.getError() != null) {
                throw new EdgeDrCommunicationException(responseMsg.getError().getErrorMessage());
            }
            
            // Add values to the cache for future responses E2EID --> GUID
            cacheE2EIDToGUIDResponses(responseMsg.getPaoToE2eId(), messageGuid);
            
            return messageGuid;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new EdgeDrCommunicationException("An unexpected error occurred while sending a broadcast message.", e);
        }
    }

    @Override
    public String sendMultiUnicastRequest(Set<SimpleDevice> simpleDeviceList, byte[] payload, EdgeUnicastPriority queuePriority, 
            EdgeUnicastPriority networkPriority, YukonUserContext userContext) throws EdgeDrCommunicationException {
        
        //Convert Set of SimpleDevices into List of PaoID's
        List<Integer> paoIds = simpleDeviceList.stream()
                                               .map(device -> device.getPaoIdentifier().getPaoId())
                                               .collect(Collectors.toList());
        
        //TODO later - clean up this logging
        log.info("Processing DER Edge Unicast Request - Paos: {}, queue priority: {}, net priority: {}, payload: {}", 
                paoIds, queuePriority, networkPriority, Arrays.toString(payload));

        final String messageGuid = UUID.randomUUID().toString();

        var requestMsg = new EdgeDrUnicastRequest(paoIds, messageGuid, payload, queuePriority, networkPriority);
        var completableFuture = new CompletableFuture<EdgeDrUnicastResponse>();
        thriftUnicastMessenger.send(requestMsg, completableFuture);

        try {
            var responseMsg = completableFuture.get(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES);

            log.debug("Received info from Porter: {}", responseMsg);

            if (responseMsg == null) {
                throw new EdgeDrCommunicationException("Response time out.");
            }
            else if (responseMsg.getError() != null) {
                throw new EdgeDrCommunicationException(responseMsg.getError().getErrorMessage());
            }

            return messageGuid;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new EdgeDrCommunicationException("An unexpected error occurred while sending a Multi Unicast message.", e);
        }
    }

    @Override
    public void sendBroadcastRequest(byte[] payload, EdgeBroadcastMessagePriority priority, YukonUserContext userContext) throws EdgeDrCommunicationException {

        // TODO later - clean up this logging
        log.info("Processing DER Edge Broadcast Request - Payload: {}, Priority: {}", Arrays.toString(payload), priority);

        final String messageGuid = UUID.randomUUID().toString();

        var requestMsg = new EdgeDrBroadcastRequest(messageGuid, payload, priority);
        var completableFuture = new CompletableFuture<EdgeDrBroadcastResponse>();
        thriftBroadcastMessenger.send(requestMsg, completableFuture);

        try {
            var responseMsg = completableFuture.get(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES);

            log.debug("Received info from Porter: {}", responseMsg);

            if (responseMsg == null) {
                throw new EdgeDrCommunicationException("Response time out.");
            }
            else if (responseMsg.getError() != null) {
                throw new EdgeDrCommunicationException(responseMsg.getError().getErrorMessage());
            }

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new EdgeDrCommunicationException("An unexpected error occurred while sending a broadcast message.", e);
        }
    }

    public void cacheE2EIDToGUIDResponses(Map<Integer, Integer> paoToE2eId, String messageGuid)
            throws EdgeDrCommunicationException {
        try {
            log.debug("Caching paoToE2eId: {} with GUID: {}", paoToE2eId, messageGuid);
            paoToE2eId.values().stream().forEach(e -> derEdgeResponseService.addCacheEntry(e, messageGuid));
            log.debug("Caching response completed");
        } catch (Exception e) {
            throw new EdgeDrCommunicationException("An unexpected error occurred while caching the response", e);
        }
    }

}
