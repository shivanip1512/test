package com.cannontech.dr.controlaudit.model;

import com.cannontech.dr.controlaudit.ControlOptOutStatus;

public class ControlDeviceDetail {
    private String deviceName;
    private String serialNumber;
    private String participationState;
    private String eventPhase;
    private ControlOptOutStatus optOutStatus;

    public ControlDeviceDetail(String deviceName, String serialNumber, String participationState, String eventPhase,
            ControlOptOutStatus optOutStatus) {
        this.deviceName = deviceName;
        this.serialNumber = serialNumber;
        this.participationState = participationState;
        this.eventPhase = eventPhase;
        this.optOutStatus = optOutStatus;
    }

    public String getDeviceName() {
        return deviceName;
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
}
