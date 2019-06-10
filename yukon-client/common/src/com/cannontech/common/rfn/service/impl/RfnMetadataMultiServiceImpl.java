package com.cannontech.common.rfn.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiRequest;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponse;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponseType;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Sets;

public class RfnMetadataMultiServiceImpl implements RfnDeviceMetadataMultiService {

    private static final String nmError = RfnDeviceMetadataServiceImpl.nmError;
    private static final String commsError = RfnDeviceMetadataServiceImpl.commsError;
        
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configSource;
    @Autowired private ScheduledExecutor executor;
    @Autowired private NextValueHelper nextValueHelper;
    
    private static final Logger log = YukonLogManager.getLogger(RfnMetadataMultiServiceImpl.class);

    private RequestReplyTemplateImpl<RfnMetadataMultiResponse> qrTemplate;    
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadata(Set<RfnIdentifier> identifiers,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        RfnMetadataMultiRequest request = getRequest(identifiers, requests);
        request.getRfnIdentifiers().addAll(identifiers);
        return getMetaData(request);
    }
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadataForGatewayRfnIdentifiers(Set<RfnIdentifier> identifiers,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        RfnMetadataMultiRequest request =  getRequest(identifiers, requests);
        request.getPrimaryNodesForGatewayRfnIdentifiers().addAll(identifiers);
        return getMetaData(request);
    }
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadata(RfnIdentifier identifier,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        return getMetadata(Sets.newHashSet(identifier), requests);
    }

    /**
     * Returns meta data
     */
    private Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetaData(RfnMetadataMultiRequest request)
            throws NmCommunicationException {
        try {
            String requestIdentifier = String.valueOf(nextValueHelper.getNextValue("RfnMetadataMultiRequest"));
            request.setRequestID(requestIdentifier);
            log.debug("RfnMetadataMultiRequest identifier {} metadatas {} device ids {} gateway ids {}", requestIdentifier, request.getRfnMetadatas(),
                request.getRfnIdentifiers().size(), request.getPrimaryNodesForGatewayRfnIdentifiers().size());
            BlockingJmsReplyHandler<RfnMetadataMultiResponse> reply =
                new BlockingJmsReplyHandler<>(RfnMetadataMultiResponse.class);
            qrTemplate.send(request, reply);
            RfnMetadataMultiResponse response = reply.waitForCompletion();
            log.debug("RfnMetadataMultiResponse identifier {} [{} out of {}] response {}", requestIdentifier,
                response.getSegmentNumber(), response.getTotalSegments(), response.getResponseType());
            validateResponse(response,requestIdentifier);
            updatePrimaryGatewayToDeviceMapping(response);
            return response.getQueryResults();
        } catch (ExecutionException e) {
            log.error(commsError, e);
            throw new NmCommunicationException(commsError, e);
        }
    }

    private RfnMetadataMultiRequest getRequest(Set<RfnIdentifier> identifiers, Set<RfnMetadataMulti> multi) {
        RfnMetadataMultiRequest request = new RfnMetadataMultiRequest();
        request.setRfnIdentifiers(new HashSet<>());
        request.setPrimaryNodesForGatewayRfnIdentifiers(new HashSet<>());
        request.setRfnMetadatas(multi);
        return request;
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
            String error = "No query results recieved reply type " + response.getResponseType() + " request identifier:"
                + requestIdentifier;
            log.error(error);
            throw new NmCommunicationException(error);
        }
    }

    private void updatePrimaryGatewayToDeviceMapping(RfnMetadataMultiResponse response) {
        executor.execute(() -> {
            Set<NodeComm> comms = response.getQueryResults().values().stream()
                    .filter(result -> result.isValidResultForMulti(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM))
                    .map(result -> {
                        return (NodeComm) result.getMetadatas().get(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM);
                }).collect(Collectors.toSet());
            
            if(!comms.isEmpty()) {
                log.debug("Updating device to gateway mapping for {} nodes", comms.size());
            }
        });
    }
    
    @PostConstruct
    public void initialize() {
        qrTemplate = new RequestReplyTemplateImpl<>(JmsApiDirectory.RF_METADATA_MULTI.getName(),
                configSource, connectionFactory, JmsApiDirectory.RF_METADATA_MULTI.getQueue().getName(), false);
    }
    
}