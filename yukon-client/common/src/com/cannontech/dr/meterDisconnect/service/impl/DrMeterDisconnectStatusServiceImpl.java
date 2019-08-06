package com.cannontech.dr.meterDisconnect.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.dr.meterDisconnect.DrMeterEventStatus;
import com.cannontech.dr.meterDisconnect.dao.DrMeterDisconnectStatusDao;
import com.cannontech.dr.meterDisconnect.service.DrMeterDisconnectStatusService;

/**
 * Service for logging the control status of disconnect meters in DR events, and retrieving those statuses.
 */
public class DrMeterDisconnectStatusServiceImpl implements DrMeterDisconnectStatusService {
    @Autowired DrMeterDisconnectStatusDao statusDao;
    
    @Override
    @Transactional
    public int initializeEvent(Instant startTime, Instant expectedEndTime, int programPaoId,
                               Collection<SimpleDevice> meters) {

        int eventId = statusDao.createEvent(startTime, expectedEndTime, programPaoId);
        List<Integer> deviceIds = meters.stream()
                                        .map(SimpleDevice::getDeviceId)
                                        .collect(Collectors.toList());
        statusDao.addDevicesToEvent(eventId, deviceIds);
        return eventId;
    }

    @Override
    public void restoreSent(Instant stopTime, int eventId) {
        statusDao.updateControlStatus(eventId, DrMeterControlStatus.RESTORE_SENT, stopTime, DrMeterControlStatus.CONTROL_CONFIRMED);
    }

    @Override
    public void updateControlStatus(int eventId, DrMeterControlStatus status, Instant timestamp,
                                    Collection<Integer> deviceIds) {
        
        statusDao.updateControlStatus(eventId, status, timestamp, deviceIds);
    }

    @Override
    public void updateAllControlTimeout(int eventId) {
        statusDao.updateControlStatus(eventId, DrMeterControlStatus.CONTROL_TIMEOUT, Instant.now(), DrMeterControlStatus.CONTROL_SENT);
    }

    @Override
    public void updateAllRestoreTimeout(int eventId) {
        statusDao.updateControlStatus(eventId, DrMeterControlStatus.RESTORE_TIMEOUT, Instant.now(), 
                                      DrMeterControlStatus.RESTORE_SENT);
    }

    @Override
    public List<DrMeterEventStatus> getAllCurrentStatusForLatestProgramEvent(int programId,
                                                                             Collection<DrMeterControlStatus> controlStatuses,
                                                                             Collection<DrMeterControlStatus> restoreStatuses) {
        return statusDao.getAllCurrentStatusForLatestProgramEvent(programId, controlStatuses, restoreStatuses);
    }

    @Override
    public Optional<Integer> findActiveEventForProgram(int programId) {
        return statusDao.findActiveEventForProgram(programId);
    }
    
    @Override
    public Optional<Integer> findActiveEventForDevice(int deviceId) {
        return statusDao.findActiveEventForDevice(deviceId);
    }

}
