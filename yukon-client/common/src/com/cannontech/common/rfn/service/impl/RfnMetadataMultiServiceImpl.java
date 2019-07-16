package com.cannontech.common.rfn.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiRequest;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponse;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponseType;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.service.BlockingJmsMultiReplyHandler;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.RequestMultiReplyTemplate;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Sets;

public class RfnMetadataMultiServiceImpl implements RfnDeviceMetadataMultiService {
    private static final Logger rfnCommsLog = YukonLogManager.getRfnLogger();
    private static final Logger log = YukonLogManager.getLogger(RfnMetadataMultiServiceImpl.class);
    private static final String nmError = RfnDeviceMetadataServiceImpl.nmError;
    private static final String commsError = RfnDeviceMetadataServiceImpl.commsError;
        
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ScheduledExecutor executor;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    
    private RequestMultiReplyTemplate<RfnMetadataMultiRequest, RfnMetadataMultiResponse> multiReplyTemplate;
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadataForDeviceRfnIdentifiers(Set<RfnIdentifier> identifiers,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        RfnMetadataMultiRequest request = getRequest(requests);
        request.getRfnIdentifiers().addAll(identifiers);
        return sendMetadataRequest(request);
    }
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadataForDeviceRfnIdentifier(RfnIdentifier identifier,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        RfnMetadataMultiRequest request = getRequest(requests);
        request.setRfnIdentifiers(Sets.newHashSet(identifier));
        return sendMetadataRequest(request);
    }
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadataForGatewayRfnIdentifiers(Set<RfnIdentifier> identifiers,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        RfnMetadataMultiRequest request = getRequest(requests);
        request.getPrimaryNodesForGatewayRfnIdentifiers().addAll(identifiers);
        return sendMetadataRequest(request);
    }
    
    /**
     * Returns meta data
     */
    private Map<RfnIdentifier, RfnMetadataMultiQueryResult> sendMetadataRequest(RfnMetadataMultiRequest request)
            throws NmCommunicationException {
        try {
            // Set up request
            String requestIdentifier = String.valueOf(nextValueHelper.getNextValue("RfnMetadataMultiRequest"));
            request.setRequestID(requestIdentifier);
            log.debug("RfnMetadataMultiRequest identifier {} metadatas {} device ids {} gateway ids {}", requestIdentifier, request.getRfnMetadatas(),
                request.getRfnIdentifiers().size(), request.getPrimaryNodesForGatewayRfnIdentifiers().size());
            
            // Send request
            BlockingJmsMultiReplyHandler<RfnMetadataMultiResponse> replyHandler = 
                    new BlockingJmsMultiReplyHandler<>(RfnMetadataMultiResponse.class);
            multiReplyTemplate.send(request, replyHandler);
            
            // Receive and validate responses. Merge into a single map. 
            List<RfnMetadataMultiResponse> responses = replyHandler.waitForCompletion();
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> metadata = new HashMap<>();
            for (RfnMetadataMultiResponse response : responses) {
                handleMetadataResponse(response, requestIdentifier);
                metadata.putAll(response.getQueryResults());
            }
            return metadata;
        } catch (ExecutionException e) {
            log.error(commsError, e);
            throw new NmCommunicationException(commsError, e);
        }
    }

    private RfnMetadataMultiRequest getRequest(Set<RfnMetadataMulti> multi) {
        RfnMetadataMultiRequest request = new RfnMetadataMultiRequest();
        request.setRfnIdentifiers(new HashSet<>());
        request.setPrimaryNodesForGatewayRfnIdentifiers(new HashSet<>());
        request.setRfnMetadatas(multi);
        return request;
    }

    private void handleMetadataResponse(RfnMetadataMultiResponse response, String requestId) throws NmCommunicationException {
        if(rfnCommsLog.isEnabled(Level.INFO)) {
            rfnCommsLog.log(Level.INFO, "<<< " + response.toString());
        }
        log.debug("RfnMetadataMultiResponse identifier {} [{} out of {}] response {}", requestId,
            response.getSegmentNumber(), response.getTotalSegments(), response.getResponseType());
        validateResponse(response, requestId);
        updatePrimaryGatewayToDeviceMapping(response);
    }
    
    private void validateResponse(RfnMetadataMultiResponse response, String requestIdentifier)
            throws NmCommunicationException {
        if (response.getResponseType() != RfnMetadataMultiResponseType.OK) {
            String error = nmError + " Reply type:" + response.getResponseType() + " Message:"
                + response.getResponseMessage() + " request identifier " + requestIdentifier;
            log.error(error);
            throw new NmCommunicationException(error);
        }
        if (response.getQueryResults() == null) {
            String error = "No query results received reply type " + response.getResponseType() + " request identifier:"
                + requestIdentifier;
            log.error(error);
            throw new NmCommunicationException(error);
        }
    }

    private void updatePrimaryGatewayToDeviceMapping(RfnMetadataMultiResponse response) {
        executor.execute(() -> {
            List<NodeComm> deviceToGateway = response.getQueryResults().values().stream()
                    .filter(result -> result.isValidResultForMulti(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM))
                    .map(result -> { return (NodeComm) result.getMetadatas().get(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM); })                  
                    .collect(Collectors.toList());
            if(!deviceToGateway.isEmpty()) {
                log.debug("Updating device to gateway mapping for {} nodes", deviceToGateway.size());
                rfnDeviceDao.saveDynamicRfnDeviceData(deviceToGateway);
            }
        });
    }
    
    @PostConstruct
    public void initialize() {
        multiReplyTemplate = new RequestMultiReplyTemplate<>(connectionFactory, JmsApiDirectory.RF_METADATA_MULTI);
    }
    
}