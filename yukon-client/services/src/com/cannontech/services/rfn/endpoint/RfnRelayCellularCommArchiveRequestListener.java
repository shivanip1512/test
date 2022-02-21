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

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.node.NodeConnectionState;
import com.cannontech.common.rfn.message.node.RelayCellularComm;
import com.cannontech.common.rfn.message.node.RfnRelayCellularCommArchiveRequest;
import com.cannontech.common.rfn.message.node.RfnRelayCellularCommArchiveResponse;
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

public class RfnRelayCellularCommArchiveRequestListener implements RfnArchiveProcessor{
    @Autowired private RfnArchiveQueueHandler queueHandler;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private Logger log = YukonLogManager.getRfnLogger(RfnRelayCellularCommArchiveRequestListener.class);
    private YukonJmsTemplate jmsTemplate;

    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createResponseTemplate(JmsApiDirectory.RFN_RELAY_CELL_COMM_ARCHIVE);
    }
    
    // map of RF Relay Cell Comm Status to Yukon CommStatusState state group
    private static Map<NodeConnectionState, CommStatusState> commStatusMapping = 
            Map.of(NodeConnectionState.NOT_ACTIVE, CommStatusState.DISCONNECTED,
                   NodeConnectionState.ACTIVE, CommStatusState.CONNECTED);

    @Override
    public void process(Object obj, String processor) {
        // received RfnRelayCellularCommArchiveRequest message from NM, persist gateway to device mapping to database, send
        // acknowledgment to NM
        processRequest((RfnRelayCellularCommArchiveRequest) obj, processor);
    }

    /**
     * Handles message from NM, logs the message and put in on a queue.
     */
    public void handleArchiveRequest(RfnRelayCellularCommArchiveRequest request) {
        log.info("<<< " + request.toString());
        queueHandler.add(this, request);
    }

    /**
     * Persists gateway to device mapping.
     */
    private void processRequest(RfnRelayCellularCommArchiveRequest request, String processor) {
        Map<Long, RelayCellularComm> wiFiComms = request.getRelayCellularComms();
        for (Map.Entry<Long, RelayCellularComm> entry : wiFiComms.entrySet()) {
            try {
                publishPointData(entry, processor);
            } catch (Exception e) {
                log.error("Unable to publish point data for device {}",
                        entry.getValue().getDeviceRfnIdentifier(), e);
            }
        }
        sendAcknowledgement(new HashSet<>(wiFiComms.keySet()), processor);
    }

    /**
     * Attempts to publish point data for the device. If unable to lookup device in cache the exception will
     * be thrown and it will continue processing entries.
     */
    private void publishPointData(Entry<Long, RelayCellularComm> entry, String processor) {
        RelayCellularComm relayCellComm = entry.getValue();
        Date commStatusTimestamp = new Date(relayCellComm.getCellularCommStatusTimestamp());
        RfnIdentifier rfnIdentifier = relayCellComm.getDeviceRfnIdentifier();

        archiveAndLogPointData(processor, rfnIdentifier, commStatusTimestamp, getForWifiCommStatus(relayCellComm.getRelayCellularCommStatus()).getRawState(), BuiltInAttribute.COMM_STATUS);
        archiveAndLogPointData(processor, rfnIdentifier, commStatusTimestamp, relayCellComm.getRsrp(), BuiltInAttribute.REFERENCE_SIGNAL_RECEIVED_POWER);
        archiveAndLogPointData(processor, rfnIdentifier, commStatusTimestamp, relayCellComm.getRsrq(), BuiltInAttribute.REFERENCE_SIGNAL_RECEIVED_QUALITY);
        archiveAndLogPointData(processor, rfnIdentifier, commStatusTimestamp, relayCellComm.getRssi(), BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR);
        archiveAndLogPointData(processor, rfnIdentifier, commStatusTimestamp, relayCellComm.getSinr(), BuiltInAttribute.SIGNAL_TO_INTERFERENCE_PLUS_NOISE_RATIO);
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

    // Archive and log the point data
    private void archiveAndLogPointData(String processor, RfnIdentifier rfnIdentifier, Date commStatusTimestamp, Integer value,
            BuiltInAttribute builtInAttribute) {
        try {
            if (value != null) {
                PointData pointData = buildPointData(rfnIdentifier, builtInAttribute, value, commStatusTimestamp);
                asyncDynamicDataSource.putValue(pointData);

                log.debug("{} generated {} {} {}", processor, pointData, value, rfnIdentifier);
            }
        } catch (IllegalUseOfAttribute e) {
            log.error("{} generation of point data for {} {} value {} failed", processor, rfnIdentifier, builtInAttribute,
                    value, e);
        }
    }

    /**
     * Sends acknowledgement to NM
     */
    private void sendAcknowledgement(Set<Long> referenceIds, String processor) {
        if (!referenceIds.isEmpty()) {
            RfnRelayCellularCommArchiveResponse response = new RfnRelayCellularCommArchiveResponse();
            response.setReferenceIDs(referenceIds);
            log.info(">>> " + response.toString());
            jmsTemplate.convertAndSend(response);
        }
    }

    /**
     * Returns the CommStatusState for the corresponding NodeWiFiCommStatus.
     * @throws NoSuchElementException
     */
    private static CommStatusState getForWifiCommStatus(NodeConnectionState relayCellCommStatus) throws NoSuchElementException {
        return Optional.ofNullable(commStatusMapping.get(relayCellCommStatus)).orElseThrow();
    }
}
