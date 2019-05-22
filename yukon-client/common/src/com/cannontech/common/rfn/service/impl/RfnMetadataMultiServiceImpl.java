package com.cannontech.common.rfn.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import com.cannontech.common.rfn.message.metadatamulti.NodeComm;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiRequest;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponse;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponseType;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.google.common.collect.Sets;

public class RfnMetadataMultiServiceImpl implements RfnDeviceMetadataMultiService {

    private static final String nmError = RfnDeviceMetadataServiceImpl.nmError;
    private static final String commsError = RfnDeviceMetadataServiceImpl.commsError;
        
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configSource;
    @Autowired private ScheduledExecutor executor;
    
    private static final Logger log = YukonLogManager.getLogger(RfnMetadataMultiServiceImpl.class);

    private RequestReplyTemplateImpl<RfnMetadataMultiResponse> qrTemplate;    
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadata(EntityType entity, Set<RfnIdentifier> identifiers,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        BlockingJmsReplyHandler<RfnMetadataMultiResponse> reply = new BlockingJmsReplyHandler<>(RfnMetadataMultiResponse.class);
        try {
            RfnMetadataMultiRequest request = new RfnMetadataMultiRequest(entity);
            request.setRfnIdentifiers(identifiers);
            request.setRfnMetadatas(requests);
            int randomIdentifier = new Random().nextInt();
            log.debug("RfnMetadataMultiRequest identifier: {}", randomIdentifier);
            qrTemplate.send(request, reply);
            RfnMetadataMultiResponse response = reply.waitForCompletion();
            log.debug("RfnMetadataMultiRequest identifier: {} response: {}", randomIdentifier, response.getResponseType());
                        
            if (response.getResponseType() == RfnMetadataMultiResponseType.OK) {
                updatePrimaryGatewayToDeviceMapping(response);
                return response.getQueryResults();
            } else {
                String error =
                    nmError + " Reply type:" + response.getResponseType() + " Message:" + response.getResponseMessage();
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