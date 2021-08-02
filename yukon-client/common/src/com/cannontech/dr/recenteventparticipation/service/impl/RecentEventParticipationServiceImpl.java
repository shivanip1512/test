package com.cannontech.dr.recenteventparticipation.service.impl;

import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.Range;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.cannontech.dr.itron.service.impl.ItronLoadControlEventStatus;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationDetail;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationStats;
import com.cannontech.dr.recenteventparticipation.model.RecentEventParticipationSummary;
import com.cannontech.dr.recenteventparticipation.service.RecentEventParticipationService;

public class RecentEventParticipationServiceImpl implements RecentEventParticipationService {
    
    @Autowired NextValueHelper nextValueHelper;
    @Autowired RecentEventParticipationDao recentEventParticipationDao;

    @Override
    public void updateDeviceControlEvent(int externalEventId, int deviceId, EventPhase eventPhase,
            Instant deviceReceivedTime) {
        ControlEventDeviceStatus receivedDeviceStatus = ControlEventDeviceStatus.getDeviceStatus(eventPhase);
        recentEventParticipationDao.updateDeviceControlEvent(String.valueOf(externalEventId), deviceId, receivedDeviceStatus,
                deviceReceivedTime, null, null);
    }

    @Override
    public void updateDeviceControlEvent(int externalEventId, int deviceId, ItronLoadControlEventStatus eventStatus,
            Instant deviceReceivedTime) {
        ControlEventDeviceStatus receivedDeviceStatus = ControlEventDeviceStatus.getDeviceStatus(eventStatus);
        recentEventParticipationDao.updateDeviceControlEvent(String.valueOf(externalEventId), deviceId, receivedDeviceStatus,
                deviceReceivedTime, null, null);
    }
    
    @Override
    @Transactional
    public void createDeviceControlEvent(int programId, String externalEventId, int groupId, Instant startTime, Instant stopTime) {
        long eventId = nextValueHelper.getNextValue("ControlEvent");
        recentEventParticipationDao.createNewEventMapping(programId, eventId, groupId, startTime, stopTime, externalEventId);
        recentEventParticipationDao.insertDeviceControlEvent(eventId, groupId, startTime);
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
    public List<RecentEventParticipationDetail> getRecentEventParticipationDetail(int eventId) {
        List<RecentEventParticipationDetail> recentEventParticipationDetails = recentEventParticipationDao.getRecentEventParticipationDetail(eventId);
        for (RecentEventParticipationDetail recentEventParticipationDetail : recentEventParticipationDetails) {
            recentEventParticipationDetail.setDeviceDetails(recentEventParticipationDao.getControlEventDeviceData(eventId));
        }
        return recentEventParticipationDetails;
    }

    @Override
    public List<RecentEventParticipationDetail> getRecentEventParticipationDetails(Range<Instant> range) {
        List<RecentEventParticipationDetail> recentEventParticipationDetails = recentEventParticipationDao.getRecentEventParticipationDetails(range);
        for (RecentEventParticipationDetail recentEventParticipationDetail : recentEventParticipationDetails) {
            recentEventParticipationDetail.setDeviceDetails(recentEventParticipationDao.getControlEventDeviceData(recentEventParticipationDetail.getControlEventId()));
        }
        return recentEventParticipationDetails;
    }

    @Override
    public int getNumberOfEvents(Range<Instant> range) {
        return recentEventParticipationDao.getNumberOfEvents(range);
    }
}
