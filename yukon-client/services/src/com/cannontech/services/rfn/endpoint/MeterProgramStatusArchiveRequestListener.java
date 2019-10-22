package com.cannontech.services.rfn.endpoint;

import java.util.UUID;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

import org.apache.logging.log4j.Logger;
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
    public void handleArchiveRequest(BytesMessage request) {
        try {
            byte[] msgBytes = new byte[(int)request.getBodyLength()];
            
            request.readBytes(msgBytes);
            
            var requestMessage = deserializer.fromBytes(msgBytes); 

            queueHandler.add(this, requestMessage);
        } catch (JMSException e) {
            log.error("Error while deserializing message", e);
        }
    }

    /**
     * Attempts to update Meter Program Status with a new status
     */
    private void processRequest(MeterProgramStatusArchiveRequest request, String processor) {
       
        int deviceId = rfnDeviceDao.getDeviceIdForRfnIdentifier(request.getRfnIdentifier());
        if (request.getStatus() == ProgrammingStatus.INITIATING) {
            MeterProgramStatus oldStatus = meterProgrammingDao.getMeterProgramStatus(deviceId);
            if (oldStatus.getLastUpdate().isBefore(request.getTimeStamp())) {
                log.info("(Request to initiate download recieved and is older then existing status. Discarding the record. Existing status {}",
                         oldStatus);
                return;
            } else {
                log.info("Updated status to Initiating for device {}.", request.getRfnIdentifier());
                meterProgrammingDao.updateMeterProgramStatusToInitiating(deviceId, request.getTimeStamp());
                return;
            }
        }
        
        StringBuilder configId = new StringBuilder(request.getConfigurationId().toString());

        MeterProgramSource prefix = MeterProgramSource.getByPrefix(Character.toString(configId.charAt(0)));
        configId.deleteCharAt(0);
        // remove prefix from config Id
        request.setConfigurationId(configId.toString());

        if (prefix == null) {
            log.error("Configuration Id {} in {} doesn't contain recognizable prefix, discarding response", configId, request);
        } else {
            MeterProgramStatus newStatus = getMeterProgramStatus(request, prefix);
            MeterProgramStatus oldStatus = null;
            try {
                oldStatus = meterProgrammingDao.getMeterProgramStatus(deviceId);
            } catch (@SuppressWarnings("unused") NotFoundException e) {
                log.info("Creating status. \nNew Status {}", newStatus);
                meterProgrammingDao.createMeterProgramStatus(newStatus);
                return;
            }

            if (oldStatus.getLastUpdate().isBefore(newStatus.getLastUpdate())) {
                log.info("Status recieved is older then existing status. Discarding the record. \nNew Status {} \nExisting status {}",
                         newStatus,
                         oldStatus);
                return;
            }

            // If a send is in progress, a failure event should not interrupt
            // the current upload. Only when the upload is complete (in Waiting
            // Verification) should failure events be recorded
            if (newStatus.getStatus() == ProgrammingStatus.FAILED && oldStatus.getStatus() == ProgrammingStatus.UPLOADING) {
                log.info("Status recieved is failure but existing status is uploading. Discarding the record. \nNew Status {} \nExisting status {}",
                         newStatus,
                         oldStatus);
                return;
            }
            MeterProgram program = meterProgrammingDao.getProgramByDeviceId(deviceId);
            if (!program.getGuid().equals(newStatus.getReportedGuid())) {
                if (newStatus.getStatus() == ProgrammingStatus.FAILED) {
                    log.info("Status recieved is failure and guids are mismatched. Discarding the record. \nNew Status {} \nExisting status {}",
                             newStatus,
                             oldStatus);
                    return;
                } else {
                    log.info("Status recieved is failure and guids are mismatched. Updating status to mismatched");
                    newStatus.setStatus(ProgrammingStatus.MISMATCHED);
                }
            }
            log.info("Updating meter program status.  \nNew Status {} \nExisting status {}", newStatus, oldStatus);
            meterProgrammingDao.updateMeterProgramStatus(newStatus);
        }

    }

    private MeterProgramStatus getMeterProgramStatus(MeterProgramStatusArchiveRequest request, MeterProgramSource prefix) {
        MeterProgramStatus status = new MeterProgramStatus();
        status.setDeviceId(rfnDeviceDao.getDeviceIdForRfnIdentifier(request.getRfnIdentifier()));
        status.setLastUpdate(new Instant(request.getTimeStamp()));
        status.setReportedGuid(UUID.fromString(request.getConfigurationId()));
        status.setSource(prefix);
        status.setError(request.getError());
        status.setStatus(prefix != MeterProgramSource.OLD_FIRMWARE ? request.getStatus() : ProgrammingStatus.FAILED);
        return status;
    }
}