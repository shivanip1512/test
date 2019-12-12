package com.cannontech.services.rfn.endpoint;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.node.NodeWiFiComm;
import com.cannontech.common.rfn.message.node.NodeWiFiCommStatus;
import com.cannontech.common.rfn.message.node.RfnNodeWiFiCommArchiveRequest;
import com.cannontech.common.rfn.message.node.RfnNodeWiFiCommArchiveResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.CommStatusState;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.services.rfn.RfnArchiveProcessor;
import com.cannontech.services.rfn.RfnArchiveQueueHandler;

@ManagedResource
public class RfnNodeWiFiCommArchiveRequestListener implements RfnArchiveProcessor {
    private static final Logger log = YukonLogManager.getLogger(RfnNodeWiFiCommArchiveRequestListener.class);
    @Autowired private RfnArchiveQueueHandler queueHandler;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    private JmsTemplate jmsTemplate;
    private Logger rfnCommsLog = YukonLogManager.getRfnLogger();
    
    // map of RF WiFi Comm Status to Yukon CommStatusState state group
    private static Map<NodeWiFiCommStatus, CommStatusState> commStatusMapping = 
            Map.of(NodeWiFiCommStatus.NOT_ACTIVE, CommStatusState.DISCONNECTED,
                   NodeWiFiCommStatus.ACTIVE, CommStatusState.CONNECTED);

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
        if (rfnCommsLog.isEnabled(Level.INFO)) {
            rfnCommsLog.log(Level.INFO, "<<< " + request.toString());
        }
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
        NodeWiFiComm wiFiComm = entry.getValue();
        RfnIdentifier rfnIdentifier = wiFiComm.getDeviceRfnIdentifier();
        double commStatusValue = getForWifiCommStatus(wiFiComm.getNodeWiFiCommStatus()).getRawState();
        try {
            RfnDevice rfnDevice = rfnDeviceLookupService.getDevice(rfnIdentifier);
            LitePoint point = attributeService.createAndFindPointForAttribute(rfnDevice, commStatus);
            pointData = new PointData();
            pointData.setId(point.getLiteID());
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setValue(commStatusValue);
            pointData.setTime(new Date(wiFiComm.getWiFiCommStatusTimestamp()));
            pointData.setType(point.getPointType());
            pointData.setTagsPointMustArchive(true);

            asyncDynamicDataSource.putValue(pointData);

            log.debug("{} generated {} {} {}", processor, pointData, commStatus, rfnIdentifier);         
        } catch (IllegalUseOfAttribute e) {
            log.error("{} generation of point data for {} {} value {} failed", processor, rfnIdentifier, commStatus,
                    commStatusValue, e);
        }
        
        return entry.getKey();
    }

    /**
     * Sends acknowledgement to NM
     */
    private void sendAcknowledgement(Set<Long> referenceIds, String processor) {
        if (!referenceIds.isEmpty()) {
            RfnNodeWiFiCommArchiveResponse response = new RfnNodeWiFiCommArchiveResponse();
            response.setReferenceIDs(referenceIds);
            log.debug("{} acknowledged ids {}", processor, response.getReferenceIDs());
            jmsTemplate.convertAndSend(JmsApiDirectory.RFN_NODE_WIFI_COMM_ARCHIVE.getResponseQueue().get().getName(), response);
        }
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
 
    /**
     * Returns the CommStatusState for the corresponding NodeWiFiCommStatus.
     * @throws NoSuchElementException
     */
    private static CommStatusState getForWifiCommStatus(NodeWiFiCommStatus nodeWiFiCommStatus) throws NoSuchElementException {
        return Optional.ofNullable(commStatusMapping.get(nodeWiFiCommStatus)).orElseThrow();
    }
}