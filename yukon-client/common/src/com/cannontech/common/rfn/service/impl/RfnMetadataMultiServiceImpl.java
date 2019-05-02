package com.cannontech.common.rfn.service.impl;

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
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.google.common.collect.Sets;

public class RfnMetadataMultiServiceImpl implements RfnDeviceMetadataMultiService {

    private static final String nmError = RfnDeviceMetadataServiceImpl.nmError;
    private static final String commsError = RfnDeviceMetadataServiceImpl.commsError;
        
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configSource;
    
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
            qrTemplate.send(request, reply);
            RfnMetadataMultiResponse response = reply.waitForCompletion();
            if (response.getResponseType() == RfnMetadataMultiResponseType.OK) {
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