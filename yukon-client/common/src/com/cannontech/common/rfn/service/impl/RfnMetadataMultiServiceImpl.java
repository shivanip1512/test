package com.cannontech.common.rfn.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.EntityType;
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
import com.google.common.collect.Lists;
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
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadata(EntityType entity, Set<RfnIdentifier> identifiers,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        BlockingJmsReplyHandler<RfnMetadataMultiResponse> reply = new BlockingJmsReplyHandler<>(RfnMetadataMultiResponse.class);
        log.debug("identifiers {}", identifiers.size());
        List<List<RfnIdentifier>> parts = Lists.partition(Lists.newArrayList(identifiers), RfnMetadataMulti.getMaxEntity(requests));
        log.debug("parts {}", parts.size());
        int requestIdentifier = nextValueHelper.getNextValue("RfnMetadataMultiRequest");
        
        NmCommunicationException exception = null;
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> result = new HashMap<>();
        for (int i = 0; i < parts.size(); i++) {
            try {
                result.putAll(getMetaData(entity, parts.get(i), requests, reply, requestIdentifier + "-" +(i +1)));
            } catch (NmCommunicationException e) {
                exception = e;
            }
        }
        
        if(result.isEmpty()) {
            //throw last exception received
            throw exception;
        }
        
        return result;
    }

    /**
     * return meta data for a "chunk" of identifiers
     */
    private Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetaData(EntityType entity,
            List<RfnIdentifier> identifiers, Set<RfnMetadataMulti> requests,
            BlockingJmsReplyHandler<RfnMetadataMultiResponse> reply, String requestIdentifier)
            throws NmCommunicationException {
        try {
            RfnMetadataMultiRequest request = new RfnMetadataMultiRequest(entity);
            request.setRequestID(requestIdentifier);
            request.setRfnIdentifiers(Sets.newHashSet(identifiers));
            request.setRfnMetadatas(requests);
            log.debug("RfnMetadataMultiRequest identifier {} metadatas {} rfn ids {}", requestIdentifier, requests,
                identifiers.size());
            qrTemplate.send(request, reply);
            RfnMetadataMultiResponse response = reply.waitForCompletion();
            log.debug("RfnMetadataMultiResponse identifier {} response {}", requestIdentifier, response.getResponseType());
              
            if (response.getResponseType() == RfnMetadataMultiResponseType.OK) {
                updatePrimaryGatewayToDeviceMapping(response);
                return response.getQueryResults();
            } else {
                String error = nmError + " Reply type:" + response.getResponseType() + " Message:"
                    + response.getResponseMessage() + "Request Identifier:" + requestIdentifier;
                log.error(error);
                throw new NmCommunicationException(error);
            }
        } catch (ExecutionException e) {
            log.error(commsError, e);
            throw new NmCommunicationException(commsError, e);
        }
    }

    private void updatePrimaryGatewayToDeviceMapping(RfnMetadataMultiResponse response) {
        executor.execute(() -> {
            List<NodeComm> nodeComms = new ArrayList<>();
            response.getQueryResults().values().forEach(value -> {
                Object comm = value.getMetadatas().get(RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM);
                if (comm != null) {
                    nodeComms.add((NodeComm) comm);
                }
            });
            log.debug("Updating device to gateway mapping for {} nodes", nodeComms.size());
            // create/update table mapping
        });
    }
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadata(EntityType entityType, RfnIdentifier identifier,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        return getMetadata(entityType, Sets.newHashSet(identifier), requests);
    }

    @PostConstruct
    public void initialize() {
        qrTemplate = new RequestReplyTemplateImpl<>(JmsApiDirectory.RF_METADATA_MULTI.getName(),
                configSource, connectionFactory, JmsApiDirectory.RF_METADATA_MULTI.getQueue().getName(), false);
    }
    
}