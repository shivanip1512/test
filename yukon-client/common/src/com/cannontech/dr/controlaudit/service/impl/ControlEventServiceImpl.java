package com.cannontech.dr.controlaudit.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.dr.controlaudit.ControlEventDeviceStatus;
import com.cannontech.dr.controlaudit.dao.ControlEventDao;
import com.cannontech.dr.controlaudit.service.ControlEventService;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.google.common.collect.ImmutableList;

public class ControlEventServiceImpl implements ControlEventService {
    @Autowired ControlEventDao controlEventDao;

    @Override
    public void updateDeviceControlEvent(int eventId, int deviceId, EventPhase eventPhase,
            Instant deviceRecievedTime) {
        ControlEventDeviceStatus recievedDeviceStatus = ControlEventDeviceStatus.getDeviceStatus(eventPhase);
        List<ControlEventDeviceStatus> skipUpdateForStatus =
            ImmutableList.copyOf(ControlEventDeviceStatus.values())
                         .stream()
                         .filter(messageStatus -> messageStatus.getMessageOrder() <= recievedDeviceStatus.getMessageOrder())
                         .collect(Collectors.toList());

        controlEventDao.updateDeviceControlEvent(eventId, deviceId, skipUpdateForStatus, recievedDeviceStatus,
            deviceRecievedTime);
    }

    @Override
    @Transactional
    public void createDeviceControlEvent(int eventId, int groupId, Instant startTime, Instant stopTime) {
        controlEventDao.createNewEventMapping(eventId, groupId, startTime, stopTime);
        controlEventDao.insertDeviceControlEvent(eventId, groupId);
    }
}
