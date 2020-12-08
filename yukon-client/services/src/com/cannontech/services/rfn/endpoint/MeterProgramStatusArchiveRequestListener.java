package com.cannontech.services.rfn.endpoint;

import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.model.MeterProgramSource;
import com.cannontech.common.device.programming.model.MeterProgramStatus;
import com.cannontech.common.device.programming.model.ProgrammingStatus;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.services.rfn.RfnArchiveProcessor;
import com.cannontech.services.rfn.RfnArchiveQueueHandler;

@ManagedResource
public class MeterProgramStatusArchiveRequestListener implements RfnArchiveProcessor {
    private static final Logger log = YukonLogManager.getLogger(MeterProgramStatusArchiveRequestListener.class);
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnArchiveQueueHandler queueHandler;
    @Autowired private MeterProgrammingDao meterProgrammingDao;
    @Autowired ThriftByteDeserializer<MeterProgramStatusArchiveRequest> deserializer;

    @Override
    public void process(Object obj, String processor) {
        processRequest((MeterProgramStatusArchiveRequest) obj, processor);
    }

    /**
     * Handles message from SM and Porter, logs the message and put in on a
     * queue.
     */
    public void handleArchiveRequest(byte[] msgBytes) {
        var requestMessage = deserializer.fromBytes(msgBytes); 

        queueHandler.add(this, requestMessage);
    }

    /**
     * Attempts to update Meter Program Status with a new status
     */
    private void processRequest(MeterProgramStatusArchiveRequest request, @SuppressWarnings("unused") String processor) {
       
        int deviceId = rfnDeviceDao.getDeviceIdForRfnIdentifier(request.getRfnIdentifier());
        StringBuilder configId = new StringBuilder(request.getConfigurationId());

        MeterProgramSource prefix = MeterProgramSource.getByPrefix(Character.toString(configId.charAt(0)));
        configId.deleteCharAt(0);
        // remove prefix from config Id
        request.setConfigurationId(configId.toString());

        if (prefix == null) {
            log.error("Configuration Id {} in {} doesn't contain recognizable prefix, discarding response", configId, request);
            return;
        }

        MeterProgramStatus newStatus = getMeterProgramStatus(request, prefix);
        MeterProgramStatus oldStatus = null;
        try {
            oldStatus = meterProgrammingDao.getMeterProgramStatus(deviceId);
        } catch (@SuppressWarnings("unused") NotFoundException e) {
            if (newStatus.getStatus() == ProgrammingStatus.IDLE || newStatus.getSource().isOldFirmware()) {
                log.info("Creating status. \nNew Status {}", newStatus);
                meterProgrammingDao.createMeterProgramStatus(newStatus);
                return;
            }
            log.info("No existing status for device, discarding non-idle status report. \nNew Status {}", newStatus);
            return;
        }

        if (!newStatus.getLastUpdate().isAfter(oldStatus.getLastUpdate())) {
            log.info("Status received is not newer then existing status. Discarding the record. \nNew Status {} \nExisting status {}",
                     newStatus,
                     oldStatus);
            return;
        }
        
        if (newStatus.getSource().isOldFirmware()) {
            if (!oldStatus.getSource().isOldFirmware()) {
                log.warn("Status received indicates old firmware, but existing status was not old firmware. \nNew Status {} \nExisting status {}",
                    newStatus,
                    oldStatus);
            }
            meterProgrammingDao.updateMeterProgramStatus(newStatus);
            return;
        }
        
        if (newStatus.getStatus() != ProgrammingStatus.IDLE) {
            if (oldStatus.getSource().isOldFirmware()) {
                log.info("Status received is not idle, but existing status was old firmware. Discarding the record. \nNew Status {} \nExisting status {}",
                        newStatus,
                        oldStatus);
               return;
            }
        }
        try {
            Instant timeoutThreshold = oldStatus.getLastUpdate().plus(Duration.standardHours(1));
            MeterProgram assignedProgram = meterProgrammingDao.getProgramByDeviceId(deviceId);
            if (!assignedProgram.getGuid().equals(newStatus.getReportedGuid())) {
                if (newStatus.getStatus() == ProgrammingStatus.FAILED) {
                    log.info("Status received is failure, but is for a GUID not currently assigned to the device. Discarding the record. \nNew Status {} \nExisting status {}",
                             newStatus,
                             oldStatus);
                    return;
                }
                if (newStatus.getStatus() != ProgrammingStatus.IDLE) {
                    log.info("Status received is not idle, but is for a GUID not currently assigned to the device. Discarding the record. \nNew Status {} \nExisting status {}",
                            newStatus,
                            oldStatus);
                    return;
                }
                if (oldStatus.getStatus() == ProgrammingStatus.UPLOADING
                        && request.getSource() == MeterProgramStatusArchiveRequest.Source.SM_STATUS_ARCHIVE
                        && newStatus.getReportedGuid().equals(oldStatus.getReportedGuid())
                        && newStatus.getLastUpdate().isBefore(timeoutThreshold)) {
                    log.info("Status received appears to be a MeterInfoStatus issued while the meter is still uploading. Discarding the record.\nNew Status{} \nExisting status{}", 
                            newStatus,
                            oldStatus);
                    return;
                }
                log.info("Status received is idle and guids are mismatched. Updating status to mismatched");
                newStatus.setStatus(ProgrammingStatus.MISMATCHED);
            } else {
                // If a send is in progress, a failure event should only interrupt the current upload
                //  after the timeout has passed.
                if (oldStatus.getStatus() == ProgrammingStatus.UPLOADING
                        && newStatus.getStatus() == ProgrammingStatus.FAILED
                        && newStatus.getLastUpdate().isBefore(timeoutThreshold)) {
                    log.info("Status received is failure, but existing status is uploading and timeout has not passed. Discarding the record. \nNew Status {} \nExisting status {}",
                             newStatus,
                             oldStatus);
                    return;
                }
            }
        } catch (@SuppressWarnings("unused") NotFoundException ex) {
            if (newStatus.getStatus() != ProgrammingStatus.IDLE) {
                log.info("Status received is not idle, but no GUID is assigned to the device. Discarding the record. \nNew Status {} \nExisting status {}",
                        newStatus,
                        oldStatus);
                return;
            }
        }
        if (newStatus.getStatus() != ProgrammingStatus.IDLE &&
            newStatus.getStatus() != ProgrammingStatus.MISMATCHED) {
            newStatus.setReportedGuid(oldStatus.getReportedGuid());
            newStatus.setSource(oldStatus.getSource());
        }
        log.info("Updating meter program status.  \nNew Status {} \nExisting status {}", newStatus, oldStatus);
        meterProgrammingDao.updateMeterProgramStatus(newStatus);
    }

    private MeterProgramStatus getMeterProgramStatus(MeterProgramStatusArchiveRequest request, MeterProgramSource prefix) {
        MeterProgramStatus status = new MeterProgramStatus();
        status.setDeviceId(rfnDeviceDao.getDeviceIdForRfnIdentifier(request.getRfnIdentifier()));
        status.setLastUpdate(new Instant(request.getTimestamp()));
        status.setReportedGuid(UUID.fromString(request.getConfigurationId()));
        status.setSource(prefix);
        status.setError(request.getError());
        status.setStatus(prefix != MeterProgramSource.OLD_FIRMWARE ? request.getStatus() : ProgrammingStatus.FAILED);
        return status;
    }
}
