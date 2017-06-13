package com.cannontech.dr.controlaudit.model;

import java.util.List;

import org.joda.time.Instant;

public class RecentEventParticipationDetail extends ControlAuditBase {

    private Instant stopTime;
    private String accountNumber;
    private List<ControlDeviceDetail> deviceDetails;

    public RecentEventParticipationDetail(int eventId, String programName, String groupName, Instant startTime, Instant stopTime,
            String accountNumber, List<ControlDeviceDetail> deviceDetails) {
        super(eventId, programName, groupName, startTime);
        this.stopTime = stopTime;
        this.accountNumber = accountNumber;
        this.deviceDetails = deviceDetails;
    }

    public Instant getStopTime() {
        return stopTime;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public List<ControlDeviceDetail> getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(List<ControlDeviceDetail> deviceDetails) {
        this.deviceDetails = deviceDetails;
    }
}
