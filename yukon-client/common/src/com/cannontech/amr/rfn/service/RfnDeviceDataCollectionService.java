package com.cannontech.amr.rfn.service;

import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_DESCENDANT_COUNT;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_GATEWAY;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.amr.rfn.message.dataRequest.RfnDeviceDataRequest;
import com.cannontech.amr.rfn.message.dataRequest.RfnDeviceDataResponse;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.ExceptionToNullHelper;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;

public class RfnDeviceDataCollectionService implements MessageListener {
    
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private RfnDeviceMetadataMultiService metadataMultiService;
    
    @Autowired protected YukonJmsTemplate jmsTemplate;
    private static final Logger log = YukonLogManager.getLogger(RfnDeviceDataCollectionService.class);
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof RfnDeviceDataRequest) {
                    
                    RfnDeviceDataRequest request = (RfnDeviceDataRequest) objMessage.getObject();
                    Instant lastUpdateTime =
                            persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.DYNAMIC_RFN_DEVICE_DATA_LAST_UPDATE_TIME);
                    
                    if (lastUpdateTime == null || request.getLastUpdateTime().isAfter(lastUpdateTime)) {
                        updateDeviceToGatewayMapping(request.getLastUpdateTime());
                        persistedSystemValueDao.setValue(PersistedSystemValueKey.DYNAMIC_RFN_DEVICE_DATA_LAST_UPDATE_TIME,
                                request.getLastUpdateTime());
                        jmsTemplate.convertAndSend(message.getJMSReplyTo(), new RfnDeviceDataResponse(true));
                    } else {
                        jmsTemplate.convertAndSend(message.getJMSReplyTo(), new RfnDeviceDataResponse());
                    }
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
                try {
                    jmsTemplate.convertAndSend(message.getJMSReplyTo(), new RfnDeviceDataResponse(false));
                } catch (JmsException | JMSException e1) {
                    log.error("Unable to send message", e1);
                }
            }
        }
    }
    
    /**
     * Sends message to NM to get device to gateway mapping information for all gateways. When message is received the device to
     * gateway mapping is persisted in DynamicRfnDeviceData.
     */
    private void updateDeviceToGatewayMapping(Instant treeGenerationEndTime) {
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        String gatewayNames = gateways.stream().map(gateway -> gateway.getName()).collect(Collectors.joining(", "));
        log.info("Sending request to NM for device to gateway mapping information for {}", gatewayNames);
        Map<RfnIdentifier, RfnDevice> gatewayIds = gateways.stream()
                .collect(Collectors.toMap(gateway -> gateway.getRfnIdentifier(), gateway -> gateway));
        try {
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> response = metadataMultiService
                    .getMetadataForGatewayRfnIdentifiers(gatewayIds.keySet(),
                            Set.of(PRIMARY_FORWARD_GATEWAY, PRIMARY_FORWARD_DESCENDANT_COUNT));
            Set<DynamicRfnDeviceData> deviceData = new HashSet<>();
            response.forEach((deviceRfnIdentifier, queryResult) -> {
                RfnDevice device = ExceptionToNullHelper.nullifyExceptions(() -> rfnDeviceCreationService.getOrCreate(deviceRfnIdentifier));
                // Li confirmed PRIMARY_FORWARD_GATEWAY and PRIMARY_FORWARD_DESCENDANT_COUNT will always be returned
                if (device != null && queryResult.isValidResultForMulti(PRIMARY_FORWARD_GATEWAY)
                        && queryResult.isValidResultForMulti(PRIMARY_FORWARD_DESCENDANT_COUNT)) {
                    RfnIdentifier gatewayRfnIdentifier = (RfnIdentifier) queryResult.getMetadatas().get(PRIMARY_FORWARD_GATEWAY);
                    // if gateway doesn't exists in Yukon, RfnIdentifier is not enough information to create gateway
                    if (gatewayIds.containsKey(gatewayRfnIdentifier)) {
                        int descendantCount = (Integer) queryResult.getMetadatas().get(PRIMARY_FORWARD_DESCENDANT_COUNT);
                        DynamicRfnDeviceData data = new DynamicRfnDeviceData(device, gatewayIds.get(gatewayRfnIdentifier),
                                descendantCount, treeGenerationEndTime);
                        deviceData.add(data);
                    }
                }
            });

            log.info("Updating device to gateway mapping information for {} devices {}", gatewayNames, deviceData.size());
            rfnDeviceDao.saveDynamicRfnDeviceData(deviceData);
            log.info("Updated device to gateway mapping information for {} devices {}", gatewayNames, deviceData.size());

        } catch (NmCommunicationException e) {
            log.error("Error while trying to send request to NM for device to gateway mapping information.", e);
        }
    }

}
