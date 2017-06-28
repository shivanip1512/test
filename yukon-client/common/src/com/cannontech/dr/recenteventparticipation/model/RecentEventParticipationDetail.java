package com.cannontech.dr.recenteventparticipation.model;

import java.util.List;

import org.joda.time.Instant;

public class RecentEventParticipationDetail extends RecentEventParticipationBase {

    private Instant stopTime;
    private List<ControlDeviceDetail> deviceDetails;

    public RecentEventParticipationDetail(int eventId, String programName, String groupName, Instant startTime,
            Instant stopTime, List<ControlDeviceDetail> deviceDetails) {
        super(eventId, programName, groupName, startTime);
        this.stopTime = stopTime;
        this.deviceDetails = deviceDetails;
    }

    public Instant getStopTime() {
        return stopTime;
    }

    public List<ControlDeviceDetail> getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(List<ControlDeviceDetail> deviceDetails) {
        this.deviceDetails = deviceDetails;
    }
}
