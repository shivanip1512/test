package com.cannontech.common.rfn.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiRequest;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponse;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponseType;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.service.BlockingJmsMultiReplyHandler;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.util.jms.RequestMultiReplyTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Sets;

public class RfnMetadataMultiServiceImpl implements RfnDeviceMetadataMultiService {
    private static final Logger log = YukonLogManager.getLogger(RfnMetadataMultiServiceImpl.class);
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private String commsError;
    private String nmError;
  
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ConfigurationSource configSource;
    @Autowired private YukonJmsTemplate jmsTemplate;
    
    private RequestMultiReplyTemplate<RfnMetadataMultiRequest, RfnMetadataMultiResponse> multiReplyTemplate;
    private RequestMultiReplyTemplate<RfnMetadataMultiRequest, RfnMetadataMultiResponse> extendedTimeoutMultiReplyTemplate;
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadataForDeviceRfnIdentifiers(Set<RfnIdentifier> identifiers,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        RfnMetadataMultiRequest request = getRequest(requests);
        request.getRfnIdentifiers().addAll(identifiers);
        return sendMetadataRequest(request, false);
    }
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadataForDeviceRfnIdentifier(RfnIdentifier identifier,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        RfnMetadataMultiRequest request = getRequest(requests);
        request.setRfnIdentifiers(Sets.newHashSet(identifier));
        return sendMetadataRequest(request, false);
    }
    
    @Override
    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getMetadataForGatewayRfnIdentifiers(Set<RfnIdentifier> identifiers,
            Set<RfnMetadataMulti> requests) throws NmCommunicationException {
        RfnMetadataMultiRequest request = getRequest(requests);
        request.getPrimaryForwardNodesForGatewayRfnIdentifiers().addAll(identifiers);
        return sendMetadataRequest(request, true);
    }
    
    /**
     * Returns meta data
     */
    private Map<RfnIdentifier, RfnMetadataMultiQueryResult> sendMetadataRequest(RfnMetadataMultiRequest request, boolean extendedTimeout)
            throws NmCommunicationException {
        try {
            // Set up request
            String requestIdentifier = String.valueOf(nextValueHelper.getNextValue("RfnMetadataMultiRequest"));
            request.setRequestID(requestIdentifier);
            log.debug("RfnMetadataMultiRequest identifier {} metadatas {} device ids {} gateway ids {}", requestIdentifier, request.getRfnMetadatas(),
                request.getRfnIdentifiers().size(), request.getPrimaryForwardNodesForGatewayRfnIdentifiers().size());
            
            // Send request
            BlockingJmsMultiReplyHandler<RfnMetadataMultiResponse> replyHandler = 
                    new BlockingJmsMultiReplyHandler<>(RfnMetadataMultiResponse.class);
            if (extendedTimeout) {
                extendedTimeoutMultiReplyTemplate.send(request, replyHandler);
            } else {
                multiReplyTemplate.send(request, replyHandler);
            }
            
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
        request.setPrimaryForwardNodesForGatewayRfnIdentifiers(new HashSet<>());
        request.setRfnMetadatas(multi);
        return request;
    }

    private void handleMetadataResponse(RfnMetadataMultiResponse response, String requestId) throws NmCommunicationException {
        int devicesInResponse = response.getQueryResults() != null ? response.getQueryResults().size() : 0;
        log.debug("RfnMetadataMultiResponse identifier {} [{} out of {}] response {} devices in response {}", requestId,
            response.getSegmentNumber(), response.getTotalSegments(), response.getResponseType(), devicesInResponse);
        validateResponse(response, requestId);
    }
    
    private void validateResponse(RfnMetadataMultiResponse response, String requestIdentifier)
            throws NmCommunicationException {
        if (response.getResponseType() != RfnMetadataMultiResponseType.OK) {
            String error = nmError + " Reply type:" + response.getResponseType() + " Message:"
                + response.getResponseMessage() + " request identifier " + requestIdentifier;
            log.error(error);
            throw new NmCommunicationException(nmError);
        }
        if (response.getQueryResults() == null) {
            String error = "No query results received reply type " + response.getResponseType() + " request identifier:"
                + requestIdentifier;
            log.error(error);
            throw new NmCommunicationException(nmError);
        }
    }

    @PostConstruct
    public void initialize() {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        commsError = messageSourceAccessor.getMessage("yukon.web.error.nm.commsError");
        nmError = messageSourceAccessor.getMessage("yukon.web.error.nm.error");
        Duration timeout = configSource.getDuration("RFN_META_DATA_REPLY_TIMEOUT", Duration.standardMinutes(2));
        Duration extendedTimeout = configSource.getDuration("RFN_META_DATA_REPLY_EXTENDED_TIMEOUT", Duration.standardMinutes(10));
        multiReplyTemplate = new RequestMultiReplyTemplate<>(jmsTemplate, null, JmsApiDirectory.RF_METADATA_MULTI,
                timeout);
        extendedTimeoutMultiReplyTemplate = new RequestMultiReplyTemplate<>(jmsTemplate, null,
                JmsApiDirectory.RF_METADATA_MULTI, extendedTimeout);
    }
}