package com.cannontech.dr.recenteventparticipation.model;

import org.joda.time.Instant;

import com.cannontech.dr.recenteventparticipation.ControlOptOutStatus;

public class ControlDeviceDetail {
    private String deviceName;
    private String accountNumber;
    private String serialNumber;
    private String participationState;
    private String eventPhase;
    private ControlOptOutStatus optOutStatus;
    private String failReason;
    private Instant retryTime;

    public ControlDeviceDetail(String deviceName, String accountNumber, String serialNumber, String participationState, String eventPhase,
            ControlOptOutStatus optOutStatus, String failReason, Instant retryTime) {
        this.deviceName = deviceName;
        this.accountNumber = accountNumber;
        this.serialNumber = serialNumber;
        this.participationState = participationState;
        this.eventPhase = eventPhase;
        this.optOutStatus = optOutStatus;
        this.failReason = failReason;
        this.retryTime = retryTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getParticipationState() {
        return participationState;
    }

    public String getEventPhase() {
        return eventPhase;
    }

    public ControlOptOutStatus getOptOutStatus() {
        return optOutStatus;
    }

    public String getFailReason() {
        return failReason;
    }

    public Instant getRetryTime() {
        return retryTime;
    }
}