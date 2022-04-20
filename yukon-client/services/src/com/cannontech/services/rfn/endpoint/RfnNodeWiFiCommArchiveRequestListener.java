package com.cannontech.services.rfn.endpoint;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.node.NodeConnectionState;
import com.cannontech.common.rfn.message.node.NodeWiFiComm;
import com.cannontech.common.rfn.message.node.RfnNodeWiFiCommArchiveRequest;
import com.cannontech.common.rfn.message.node.RfnNodeWiFiCommArchiveResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.services.rfn.RfnArchiveProcessor;
import com.cannontech.services.rfn.RfnArchiveQueueHandler;

@ManagedResource
public class RfnNodeWiFiCommArchiveRequestListener implements RfnArchiveProcessor {
    @Autowired private RfnArchiveQueueHandler queueHandler;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private Logger log = YukonLogManager.getRfnLogger();
    private YukonJmsTemplate jmsTemplate;

    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createResponseTemplate(JmsApiDirectory.RFN_NODE_WIFI_COMM_ARCHIVE);
    }
    
    // map of RF WiFi Comm Status to Yukon CommStatusState state group
    private static Map<NodeConnectionState, CommStatusState> commStatusMapping = 
            Map.of(NodeConnectionState.NOT_ACTIVE, CommStatusState.DISCONNECTED,
                   NodeConnectionState.ACTIVE, CommStatusState.CONNECTED);

    @Override
    public void process(Object obj, String processor) {
        // received RfnNodeWiFiCommArchiveRequest message from NM, persist gateway to device mapping to database, send
        // acknowledgment to NM
        processRequest((RfnNodeWiFiCommArchiveRequest) obj, processor);
    }

    /**
     * Handles message from NM, logs the message and put in on a queue.
     */
    public void handleArchiveRequest(RfnNodeWiFiCommArchiveRequest request) {
        log.info("<<< {}", request.toString());
        queueHandler.add(this, request);
    }

    /**
     * Persists gateway to device mapping.
     */
    private void processRequest(RfnNodeWiFiCommArchiveRequest request, String processor) {
        Set<Long> referenceIds = new HashSet<>();
        Map<Long, NodeWiFiComm> wiFiComms = request.getNodeWiFiComms();
        for (Map.Entry<Long, NodeWiFiComm> entry : wiFiComms.entrySet()) {
            referenceIds.add(publishPointData(entry, processor));
        }
        
        sendAcknowledgement(referenceIds, processor);
    }

    /**
     * Attempts to publish point data for the device. If unable to lookup device in cache the exception will
     * be thrown and it will continue processing entries.
     */
    private Long publishPointData(Entry<Long, NodeWiFiComm> entry, String processor) {
        PointData pointData = null;
        BuiltInAttribute commStatus = BuiltInAttribute.COMM_STATUS;
        BuiltInAttribute rssi = BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR;
        NodeWiFiComm wiFiComm = entry.getValue();
        Date commStatusTimestamp = new Date(wiFiComm.getWiFiCommStatusTimestamp());
        RfnIdentifier rfnIdentifier = wiFiComm.getDeviceRfnIdentifier();
        double commStatusValue = getForWifiCommStatus(wiFiComm.getNodeWiFiCommStatus()).getRawState();
        Integer rssiValue = wiFiComm.getRssi();
        try {
            pointData = buildPointData(rfnIdentifier, commStatus, commStatusValue, commStatusTimestamp);
            asyncDynamicDataSource.putValue(pointData);

            log.debug("{} generated {} {} {}", processor, pointData, commStatus, rfnIdentifier);

            // if the RSSI value is not null then archive it
            if (rssiValue != null) {
                pointData = buildPointData(rfnIdentifier, rssi, rssiValue, commStatusTimestamp);
                asyncDynamicDataSource.putValue(pointData);

                log.debug("{} generated {} {} {}", processor, pointData, rssi, rfnIdentifier);
            }
        } catch (IllegalUseOfAttribute e) {
            log.error("{} generation of point data for {} {} value {} failed", processor, rfnIdentifier, commStatus,
                    commStatusValue, e);

            if (rssiValue != null) {
                log.error("{} generation of point data for {} {} value {} failed", processor, rfnIdentifier, rssi,
                        rssiValue, e);
            }
        }

        return entry.getKey();
    }

    // Build the point data for publishing
    private PointData buildPointData(RfnIdentifier rfnIdentifier, BuiltInAttribute attribute, double value, Date timestamp)
            throws IllegalArgumentException {
        RfnDevice rfnDevice = rfnDeviceLookupService.getDevice(rfnIdentifier);
        LitePoint point = attributeService.createAndFindPointForAttribute(rfnDevice, attribute);
        PointData pointData = new PointData();
        pointData.setId(point.getLiteID());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setValue(value);
        pointData.setTime(timestamp);
        pointData.setType(point.getPointType());
        pointData.setTagsPointMustArchive(false);
        return pointData;
    }

    /**
     * Sends acknowledgement to NM
     */
    private void sendAcknowledgement(Set<Long> referenceIds, String processor) {
        if (!referenceIds.isEmpty()) {
            RfnNodeWiFiCommArchiveResponse response = new RfnNodeWiFiCommArchiveResponse();
            response.setReferenceIDs(referenceIds);
            log.debug("{} acknowledged ids {}", processor, response.getReferenceIDs());
            log.info(">>> {}", response.toString());
            jmsTemplate.convertAndSend(response);
        }
    }

    /**
     * Returns the CommStatusState for the corresponding NodeWiFiCommStatus.
     * @throws NoSuchElementException
     */
    private static CommStatusState getForWifiCommStatus(NodeConnectionState nodeWiFiCommStatus) throws NoSuchElementException {
        return Optional.ofNullable(commStatusMapping.get(nodeWiFiCommStatus)).orElseThrow();
    }
}