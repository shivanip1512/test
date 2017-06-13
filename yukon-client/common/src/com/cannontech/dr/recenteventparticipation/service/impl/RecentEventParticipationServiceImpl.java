package com.cannontech.dr.recenteventparticipation.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.Range;
import com.cannontech.dr.controlaudit.ControlEventDeviceStatus;
import com.cannontech.dr.controlaudit.model.RecentEventParticipationDetail;
import com.cannontech.dr.controlaudit.model.RecentEventParticipationStats;
import com.cannontech.dr.controlaudit.model.RecentEventParticipationSummary;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.dr.recenteventparticipation.service.RecentEventParticipationService;
import com.google.common.collect.ImmutableList;

public class RecentEventParticipationServiceImpl implements RecentEventParticipationService {
    @Autowired RecentEventParticipationDao recentEventParticipationDao;

    @Override
    public void updateDeviceControlEvent(int eventId, int deviceId, EventPhase eventPhase,
            Instant deviceRecievedTime) {
        ControlEventDeviceStatus recievedDeviceStatus = ControlEventDeviceStatus.getDeviceStatus(eventPhase);
        List<ControlEventDeviceStatus> skipUpdateForStatus =
            ImmutableList.copyOf(ControlEventDeviceStatus.values())
                         .stream()
                         .filter(messageStatus -> messageStatus.getMessageOrder() <= recievedDeviceStatus.getMessageOrder())
                         .collect(Collectors.toList());

        recentEventParticipationDao.updateDeviceControlEvent(eventId, deviceId, skipUpdateForStatus, recievedDeviceStatus,
            deviceRecievedTime);
    }

    @Override
    @Transactional
    public void createDeviceControlEvent(int eventId, int groupId, Instant startTime, Instant stopTime) {
        recentEventParticipationDao.createNewEventMapping(eventId, groupId, startTime, stopTime);
        recentEventParticipationDao.insertDeviceControlEvent(eventId, groupId);
    }

    @Override
    public List<RecentEventParticipationSummary> getRecentEventParticipationSummary(int numberOfEvents) {
        return recentEventParticipationDao.getRecentEventParticipationSummary(numberOfEvents);
    }

    @Override
    public List<RecentEventParticipationStats> getRecentEventParticipationStats(Range<Instant> range, PagingParameters pagingParameters) {
        return recentEventParticipationDao.getRecentEventParticipationStats(range, pagingParameters);
    }

    @Override
    public RecentEventParticipationDetail getRecentEventParticipationDetail(int eventId) {
        RecentEventParticipationDetail recentEventParticipationDetail = recentEventParticipationDao.getRecentEventParticipationDetail(eventId);
        recentEventParticipationDetail.setDeviceDetails(recentEventParticipationDao.getControlEventDeviceData(eventId));
        return recentEventParticipationDetail;
    }

    @Override
    public List<RecentEventParticipationDetail> getRecentEventParticipationDetails(Range<Instant> range) {
        List<RecentEventParticipationDetail> recentEventParticipationDetails = recentEventParticipationDao.getRecentEventParticipationDetails(range);
        for (RecentEventParticipationDetail recentEventParticipationDetail : recentEventParticipationDetails) {
            recentEventParticipationDetail.setDeviceDetails(recentEventParticipationDao.getControlEventDeviceData(recentEventParticipationDetail.getControlEventId()));
        }
        return recentEventParticipationDetails;
    }
}
