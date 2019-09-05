package com.cannontech.services.rfn.endpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.status.RfnStatusArchiveRequest;
import com.cannontech.amr.rfn.message.status.RfnStatusArchiveResponse;
import com.cannontech.amr.rfn.message.status.type.DemandResetStatus;
import com.cannontech.amr.rfn.message.status.type.MeterInfoStatus;
import com.cannontech.amr.rfn.message.status.type.RfnMeterDisconnectMeterMode;
import com.cannontech.amr.rfn.message.status.type.RfnMeterDisconnectStateType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest.Source;
import com.cannontech.common.device.programming.model.ProgrammingStatus;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.services.rfn.RfnArchiveProcessor;
import com.cannontech.services.rfn.RfnArchiveQueueHandler;
import com.cannontech.yukon.IDatabaseCache;

@ManagedResource
public class RfnStatusArchiveRequestListener implements RfnArchiveProcessor {
    private static final Logger log = YukonLogManager.getLogger(RfnStatusArchiveRequestListener.class);
    @Autowired private RfnArchiveQueueHandler queueHandler;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private IDatabaseCache dbCache;
    private JmsTemplate jmsTemplate;
    private Logger rfnCommsLog = YukonLogManager.getRfnLogger();
    /**
     * Meter Mode                       Relay Status    RfnMeterDisconnectState                                 Comments
        TERMINATE                       TERMINATED      DISCONNECTED(2)                                         This meter is configured for On-demand disconnect. The mode is reflecting the relay status. If it does not, it is an error.
        ARM                             ARMED           ARMED(3)                                                This meter is configured for On-demand disconnect. The mode is reflecting the relay status. If it does not, it is an error.
        RESUME                          RESUMED         CONNECTED(1)                                            This meter is configured for On-demand disconnect. The mode is reflecting the relay status. If it does not, it is an error.
        ON_DEMAND_CONFIGURATION         TERMINATED      DISCONNECTED(2)                                         This meter is configured for On-demand disconnect. Use the relay status.
        ON_DEMAND_CONFIGURATION         ARMED           ARMED(3)                                                This meter is configured for On-demand disconnect. Use the relay status.
        ON_DEMAND_CONFIGURATION         RESUMED         CONNECTED(1)                                            This meter is configured for On-demand disconnect. Use the relay status.
        ON_DEMAND_CONFIGURATION         UNKNOWN                                                                 This meter is configured for On-demand disconnect, but the relay status is not known. Ignore.
        DEMAND_THRESHOLD_CONFIGURATION  *                                                                       This meter is configured for Demand Threshold disconnect. We do not know if the mode is active or deactive yet. Ignore.
        DEMAND_THRESHOLD_ACTIVATE       TERMINATED      DISCONNECTED_DEMAND_THRESHOLD_ACTIVE(4)                 This meter is configured for Demand Threshold disconnect, and the mode is active.
        DEMAND_THRESHOLD_ACTIVATE       RESUMED         CONNECTED_DEMAND_THRESHOLD_ACTIVE(5)                    This meter is configured for Demand Threshold disconnect, and the mode is active.
        DEMAND_THRESHOLD_DEACTIVATE     *               CONNECTED(1)                                            This meter is configured for Demand Threshold disconnect, but the mode is not active, and therefore cannot cause the relay to disconnect. Customer needs to send a disconnect command to activate the mode.
        CYCLING_CONFIGURATION           *                                                                       This meter is configured for Cycling disconnect. We do not know if the mode is active or deactive yet. Ignore.
        CYCLING_ACTIVATE                TERMINATED       DISCONNECTED_CYCLING_ACTIVE(6)                         This meter is configured for Cycling disconnect, and the mode is active.
        CYCLING_ACTIVATE                RESUMED          CONNECTED_CYCLING_ACTIVE(7)                            This meter is configured for Cycling disconnect, and the mode is active.
        CYCLING_DEACTIVATE              *                CONNECTED(1)                                           This meter is configured for Cycling disconnect, but the mode is not active, and therefore cannot cause the relay to disconnect. Customer needs to send a disconnect command to activate the mode.

     */
    private Map<Pair<RfnMeterDisconnectMeterMode, RfnMeterDisconnectStateType>, RfnMeterDisconnectState> disconnectStates = new HashMap<>();
    {
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.TERMINATE, RfnMeterDisconnectStateType.TERMINATED), RfnMeterDisconnectState.DISCONNECTED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.ARM, RfnMeterDisconnectStateType.ARMED), RfnMeterDisconnectState.ARMED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.RESUME, RfnMeterDisconnectStateType.RESUMED), RfnMeterDisconnectState.CONNECTED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.ON_DEMAND_CONFIGURATION, RfnMeterDisconnectStateType.TERMINATED), RfnMeterDisconnectState.DISCONNECTED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.ON_DEMAND_CONFIGURATION, RfnMeterDisconnectStateType.ARMED), RfnMeterDisconnectState.ARMED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.ON_DEMAND_CONFIGURATION, RfnMeterDisconnectStateType.RESUMED), RfnMeterDisconnectState.CONNECTED);
        //skipping ON_DEMAND_CONFIGURATION         UNKNOWN 
        //skipping DEMAND_THRESHOLD_CONFIGURATION  *
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_ACTIVATE, RfnMeterDisconnectStateType.TERMINATED), RfnMeterDisconnectState.DISCONNECTED_DEMAND_THRESHOLD_ACTIVE);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_ACTIVATE, RfnMeterDisconnectStateType.RESUMED), RfnMeterDisconnectState.CONNECTED_DEMAND_THRESHOLD_ACTIVE);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_DEACTIVATE, RfnMeterDisconnectStateType.ARMED), RfnMeterDisconnectState.CONNECTED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_DEACTIVATE, RfnMeterDisconnectStateType.TERMINATED), RfnMeterDisconnectState.CONNECTED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_DEACTIVATE, RfnMeterDisconnectStateType.RESUMED), RfnMeterDisconnectState.CONNECTED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.DEMAND_THRESHOLD_DEACTIVATE, RfnMeterDisconnectStateType.UNKNOWN), RfnMeterDisconnectState.CONNECTED);
        //skipping CYCLING_CONFIGURATION           *   
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.CYCLING_ACTIVATE, RfnMeterDisconnectStateType.TERMINATED), RfnMeterDisconnectState.DISCONNECTED_CYCLING_ACTIVE);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.CYCLING_ACTIVATE, RfnMeterDisconnectStateType.RESUMED), RfnMeterDisconnectState.CONNECTED_CYCLING_ACTIVE);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.CYCLING_DEACTIVATE, RfnMeterDisconnectStateType.ARMED), RfnMeterDisconnectState.CONNECTED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.CYCLING_DEACTIVATE, RfnMeterDisconnectStateType.TERMINATED), RfnMeterDisconnectState.CONNECTED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.CYCLING_DEACTIVATE, RfnMeterDisconnectStateType.RESUMED), RfnMeterDisconnectState.CONNECTED);
        disconnectStates.put(Pair.of(RfnMeterDisconnectMeterMode.CYCLING_DEACTIVATE, RfnMeterDisconnectStateType.UNKNOWN), RfnMeterDisconnectState.CONNECTED);
    }

    @Override
    public void process(Object obj, String processor) {
        processRequest((RfnStatusArchiveRequest) obj, processor);
    }

    /**
     * Handles message from NM, logs the message and put in on a queue.
     */
    public void handleArchiveRequest(RfnStatusArchiveRequest request) {
        if (rfnCommsLog.isEnabled(Level.INFO)) {
            rfnCommsLog.log(Level.INFO, "<<< " + request.toString());
        }
        queueHandler.add(this, request);
    }

    /**
     * Attempts publish the point data received from NM.
     */
    private void processRequest(RfnStatusArchiveRequest request, String processor) {
        if (!request.getRfnIdentifier().is_Empty_() && request.getStatus() != null) {
            if (request.getStatus() instanceof DemandResetStatus) {
                DemandResetStatus status = (DemandResetStatus) request.getStatus();
                if (status.getData() != null) {
                    int value = status.getData().getDemandResetStatusCodeID();
                    publishPointData(value, BuiltInAttribute.RF_DEMAND_RESET_STATUS, request.getRfnIdentifier(),
                        status.getTimeStamp(), processor);
                }
            } else if (request.getStatus() instanceof MeterInfoStatus) {
                MeterInfoStatus status = (MeterInfoStatus) request.getStatus();
				updateDisconnectInfo(request, processor, status);
				archiveProgramStatus(status);
            }
        }
        sendAcknowledgement(request, processor);
    }

    /**
     * Attempts to publish disconnect point data
     */
	private void updateDisconnectInfo(RfnStatusArchiveRequest request, String processor, MeterInfoStatus status) {
		if (status.getData() != null && status.getData().getMeterDisconnectStatus() != null) {
			Pair<RfnMeterDisconnectMeterMode, RfnMeterDisconnectStateType> key = Pair.of(
					status.getData().getMeterDisconnectStatus().getMeterMode(),
					status.getData().getMeterDisconnectStatus().getRelayStatus());
			RfnMeterDisconnectState state = disconnectStates.get(key);
			if (state != null) {
				publishPointData(state.getRawState(), BuiltInAttribute.DISCONNECT_STATUS,
						request.getRfnIdentifier(), status.getTimeStamp(), processor);
			} else {
				log.info(
						"Attempt to publish point data for disconnect status {} failed. Disconnect state doesn't exist for combination {}",
						status, key);
			}
		}
	}
    
    /**
     * Sends status update message to SM to update MeterProgramStatus table
     */
	private void archiveProgramStatus(MeterInfoStatus status) {
		 if (status.getData() != null && status.getData().getMeterConfigurationID() != null) {
			MeterProgramStatusArchiveRequest request = new MeterProgramStatusArchiveRequest();
			request.setSource(Source.SM_STATUS_ARCHIVE);
			request.setRfnIdentifier(status.getRfnIdentifier());
			request.setConfigurationId(status.getData().getMeterConfigurationID());
			request.setStatus(ProgrammingStatus.IDLE);
			request.setTimeStamp(status.getTimeStamp());
			log.debug("Sending {} on queue {}", request, JmsApiDirectory.METER_PROGRAM_STATUS_ARCHIVE.getQueue().getName());
			jmsTemplate.convertAndSend(JmsApiDirectory.METER_PROGRAM_STATUS_ARCHIVE.getQueue().getName(), request);
		} else {
			log.info("Attempt to update meter programming status {} failed. MeterConfigurationID doesn't exist",
					status);
		}
	}

    /**
     * Attempts to publish point data for the device. If unable to lookup device in cache the exception will
     * be thrown and acknowledgement
     * will not be sent to NM.
     */
    private void publishPointData(int value, BuiltInAttribute attribute, RfnIdentifier rfnIdentifier, long timeStamp,
            String processor) {
        PointData pointData = null;
        try {
            Integer id = rfnDeviceDao.getDeviceIdForRfnIdentifier(rfnIdentifier);
            LiteYukonPAObject device = dbCache.getAllPaosMap().get(id);
            LitePoint point = attributeService.getPointForAttribute(device, attribute);
            pointData = new PointData();
            pointData.setId(point.getLiteID());
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setValue(value);
            pointData.setTime(new Date(timeStamp));
            pointData.setType(point.getPointType());
            pointData.setTagsPointMustArchive(true);

            asyncDynamicDataSource.putValue(pointData);

            log.debug("{} generated {} {} {}", processor, pointData, attribute, rfnIdentifier);
        } catch (IllegalUseOfAttribute e) {
            log.error("{} generation of point data for {} {} value {} failed", processor, rfnIdentifier, attribute,
                value, e);
        }
    }

    /**
     * Sends acknowledgement to NM
     */
    private void sendAcknowledgement(RfnStatusArchiveRequest request, String processor) {
        RfnStatusArchiveResponse response = new RfnStatusArchiveResponse();
        response.setStatusPointId(request.getStatusPointId());
        if (request.getRfnIdentifier().is_Empty_()) {
            log.info("{} acknowledged empty rfnIdentifier {} statusPointId={}", processor, request.getRfnIdentifier(),
                request.getStatusPointId());
        } else {
            log.debug("{} acknowledged statusPointId={}", processor, request.getStatusPointId());
        }
        jmsTemplate.convertAndSend(JmsApiDirectory.RFN_STATUS_ARCHIVE.getResponseQueue().get().getName(), response);
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}