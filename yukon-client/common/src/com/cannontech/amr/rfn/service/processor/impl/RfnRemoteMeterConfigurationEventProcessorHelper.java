package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.rfn.message.event.DetailedConfigurationStatusCode;
import com.cannontech.amr.rfn.message.event.DetailedConfigurationStatusCode.Status;
import com.cannontech.amr.rfn.message.event.MeterConfigurationStatus;
import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
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
import com.cannontech.database.db.point.stategroup.MeterProgramming;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.messaging.serialization.thrift.serializer.MeterProgramStatusArchiveRequestSerializer;
import com.google.common.collect.ImmutableMap;

public abstract class RfnRemoteMeterConfigurationEventProcessorHelper extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {

    private static final Logger log = YukonLogManager
            .getLogger(RfnRemoteMeterConfigurationEventProcessorHelper.class);
    private ThriftRequestTemplate<MeterProgramStatusArchiveRequest> thriftMessenger;

    private Map<Status, DeviceError> statusCodesToErrors = ImmutableMap.<Status, DeviceError>builder()

            // .put(Status.SUCCESS, DeviceError.UNKNOWN)
            .put(Status.REASON_UNKNOWN, DeviceError.REASON_UNKNOWN)
            .put(Status.SERVICE_NOT_SUPPORTED, DeviceError.SERVICE_UNSUPPORTED)
            .put(Status.INSUFFICIENT_SECURITY_CLEARANCE, DeviceError.INSUFFICIENT_SECURITY_CLEARANCE)
            .put(Status.OPERATION_NOT_POSSIBLE, DeviceError.OPERATION_NOT_POSSIBLE)
            .put(Status.INAPPROPRIATE_ACTION_REQUESTED, DeviceError.INAPPROPRIATE_ACTION_REQUESTED)
            .put(Status.DEVICE_BUSY, DeviceError.DEVICE_BUSY)
            .put(Status.DATA_NOT_READY, DeviceError.DATA_NOT_READY)
            .put(Status.RENEGOTIATE_REQUEST, DeviceError.RENEGOTIATE_REQUEST)
            .put(Status.INVALID_SERVICE_SEQUENCE_STATE, DeviceError.INVALID_SERVICE_SEQUENCE)
            .put(Status.DOWNLOAD_ABORTED, DeviceError.DOWNLOAD_ABORTED)
            .put(Status.FILE_TOO_LARGE, DeviceError.FILE_TOO_LARGE)
            .put(Status.CONFIGURATION_IN_PROGRESS, DeviceError.CONFIGURATION_IN_PROGRESS)
            .put(Status.UNABLE_TO_GET_FILE, DeviceError.UNABLE_TO_GET_FILE)
            .put(Status.FILE_EXPIRED, DeviceError.FILE_EXPIRED)
            .put(Status.FAILED_REQUIREMENTS, DeviceError.FAILED_REQUIREMENTS)
            .put(Status.MALFORMED_RECORD_IN_CONFIGURATION_FILE, DeviceError.MALFORMED_CONFIG_FILE_RECORD)
            .put(Status.VERIFICATION_FAILED, DeviceError.VERIFICATION_FAILED)
            .put(Status.WRITE_KEY_FAILED, DeviceError.WRITE_KEY_FAILED)
            .put(Status.CATASTROPHIC_FAILURE_FULL_REPROGRAM_REQUIRED, DeviceError.CATASTROPHIC_FAILURE)
            .build();

    @Override
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now) {
        Instant eventInstant = instantOf(event);

        var meterConfigurationId = (String) getEventDataWithType(event, RfnConditionDataType.METER_CONFIGURATION_ID);
        var meterConfigurationStatus = (MeterConfigurationStatus) getEventDataWithType(event,
                RfnConditionDataType.METER_CONFIGURATION_STATUS);

        log.info("Remote Meter Configuration failed for device={}, meterConfigurationId={}, status={}", device,
                meterConfigurationId, meterConfigurationStatus);

        archiveProgramStatus(device, meterConfigurationId, meterConfigurationStatus);

        MeterProgramming attemptStatus = translateDetailStatus(meterConfigurationStatus.getDetailedConfigurationStatusCode());
        
        rfnMeterEventService.processAttributePointData(device,
                pointDatas,
                BuiltInAttribute.METER_PROGRAMMING_ATTEMPTED,
                eventInstant,
                attemptStatus.getRawState(),
                now);
    }

    private MeterProgramming translateDetailStatus(DetailedConfigurationStatusCode detailedConfigurationStatusCode) {
        return (detailedConfigurationStatusCode.getStatus() == DetailedConfigurationStatusCode.Status.SUCCESS) 
                ? MeterProgramming.SUCCESS 
                : MeterProgramming.FAILURE;
    }

    /**
     * Sends status update message to SM to update MeterProgramStatus table
     */
    private void archiveProgramStatus(RfnDevice device, String meterConfigurationId,
            MeterConfigurationStatus meterConfigurationStatus) {
        if (meterConfigurationStatus.getDetailedConfigurationStatusCode() != null
                && meterConfigurationStatus.getDetailedConfigurationStatusCode().getStatus() != null
                && meterConfigurationId != null) {
            var detail = meterConfigurationStatus.getDetailedConfigurationStatusCode().getStatus();
            MeterProgramStatusArchiveRequest request = new MeterProgramStatusArchiveRequest();
            request.setSource(Source.SM_CONFIG_FAILURE);
            request.setRfnIdentifier(device.getRfnIdentifier());
            request.setConfigurationId(meterConfigurationId);
            if (detail == DetailedConfigurationStatusCode.Status.SUCCESS) {
                request.setStatus(ProgrammingStatus.IDLE);
                request.setError(DeviceError.SUCCESS);
            } else {
                request.setStatus(ProgrammingStatus.FAILED);
                request.setError(statusCodesToErrors.get(detail));
            }
            request.setTimestamp(Instant.now());
            log.debug("Sending {} on queue {}", request, thriftMessenger.getRequestQueueName());
            thriftMessenger.send(request);
        } else {
            log.info(
                    "Failed to update program status, insufficient information for the update for device={}, meterConfigurationId={}, status={}",
                    device, meterConfigurationId, meterConfigurationStatus);
        }
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        thriftMessenger = new ThriftRequestTemplate<>(connectionFactory,
                JmsApiDirectory.METER_PROGRAM_STATUS_ARCHIVE.getQueue().getName(),
                new MeterProgramStatusArchiveRequestSerializer());
    }
}