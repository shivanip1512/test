package com.cannontech.amr.rfn.service.processor.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.rfn.message.event.DetailedConfigurationStatusCode.Status;
import com.cannontech.amr.rfn.message.event.MeterConfigurationStatus;
import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest.Source;
import com.cannontech.common.device.programming.model.ProgrammingStatus;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.jms.ThriftRequestTemplate;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.message.dispatch.message.PointData;

public class RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    private static final Logger log = YukonLogManager.getLogger(RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor.class);
    private ThriftRequestTemplate<MeterProgramStatusArchiveRequest> thriftMessenger;
    
    private Map<Status, DeviceError> statusCodesToErrors = new HashMap<>();
    {
    	//statusCodesToErrors.put(Status.SUCCESS, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.REASON_UNKNOWN, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.SERVICE_NOT_SUPPORTED, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.INSUFFICIENT_SECURITY_CLEARANCE, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.OPERATION_NOT_POSSIBLE, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.INAPPROPRIATE_ACTION_REQUESTED, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.DEVICE_BUSY, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.DATA_NOT_READY, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.RENEGOTIATE_REQUEST, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.INVALID_SERVICE_SEQUENCE_STATE, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.DOWNLOAD_ABORTED, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.FILE_TOO_LARGE, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.INSUFFICIENT_SECURITY_CLEARANCE, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.CONFIGURATION_IN_PROGRESS, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.UNABLE_TO_GET_FILE, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.FILE_EXPIRED, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.FAILED_REQUIREMENTS, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.MALFORMED_RECORD_IN_CONFIGURATION_FILE, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.VERIFICATION_FAILED, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.WRITE_KEY_FAILED, DeviceError.UNKNOWN);
    	statusCodesToErrors.put(Status.CATASTROPHIC_FAILURE_FULL_REPROGRAM_REQUIRED, DeviceError.UNKNOWN);
    }
    
    @Override
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now) {
        Instant eventInstant = instantOf(event);
        
        var meterConfigurationId = (String) getEventDataWithType(event, RfnConditionDataType.METER_CONFIGURATION_ID);
        var meterConfigurationStatus = (MeterConfigurationStatus) getEventDataWithType(event, RfnConditionDataType.METER_CONFIGURATION_STATUS); 
        
        log.info("Remote Meter Configuration failed for device={}, meterConfigurationId={}, status={}", device, meterConfigurationId, meterConfigurationStatus);
        
		archiveProgramStatus(device, meterConfigurationId, meterConfigurationStatus);
        
        rfnMeterEventService.processAttributePointData(device, 
                                                       pointDatas, 
                                                       BuiltInAttribute.REMOTE_METER_CONFIGURATION_FAILURE, 
                                                       eventInstant, 
                                                       EventStatus.ACTIVE.getRawState(),
                                                       now);
    }

    /**
     * Sends status update message to SM to update MeterProgramStatus table
     */
	private void archiveProgramStatus(RfnDevice device, String meterConfigurationId,
			MeterConfigurationStatus meterConfigurationStatus) {
		if (meterConfigurationStatus.getDetailedConfigurationStatusCode() != null
				&& meterConfigurationStatus.getDetailedConfigurationStatusCode().getStatus() != null
				&& meterConfigurationId != null) {
			Status status = meterConfigurationStatus.getDetailedConfigurationStatusCode().getStatus();
			MeterProgramStatusArchiveRequest request = new MeterProgramStatusArchiveRequest();
			request.setSource(Source.SM_CONFIG_FAILURE);
			request.setRfnIdentifier(device.getRfnIdentifier());
			request.setConfigurationId(meterConfigurationId);
			if (status == Status.SUCCESS) {
				request.setStatus(ProgrammingStatus.IDLE);
			} else {
				request.setStatus(ProgrammingStatus.FAILED);
				request.setError(statusCodesToErrors.get(status));
			}
			request.setTimeStamp(System.currentTimeMillis());
			log.debug("Sending {} on queue {}", request, thriftMessenger.getRequestQueueName());
			thriftMessenger.send(request);
		} else {
			 log.info("Failed to update program status, insufficient information for the update for device={}, meterConfigurationId={}, status={}", device, meterConfigurationId, meterConfigurationStatus);
		}
	}
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.REMOTE_METER_CONFIGURATION_FAILURE;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        thriftMessenger = new ThriftRequestTemplate<>(connectionFactory, JmsApiDirectory.METER_PROGRAM_STATUS_ARCHIVE.getQueue().getName());
    }
}
