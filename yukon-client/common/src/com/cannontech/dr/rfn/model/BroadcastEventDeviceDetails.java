package com.cannontech.dr.rfn.model;

import org.joda.time.Instant;

import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.dr.rfn.model.UnknownStatus;

public class BroadcastEventDeviceDetails {

    private PerformanceVerificationMessageStatus messageStatus;
    private LiteLmHardware hardware;
    private Instant lastComm;
    private UnknownStatus deviceStatus;
    
    public BroadcastEventDeviceDetails(PerformanceVerificationMessageStatus messageStatus, LiteLmHardware hardware,
            Instant lastComm, UnknownStatus deviceStatus) {
        this.messageStatus = messageStatus;
        this.hardware = hardware;
        this.lastComm = lastComm;
        this.deviceStatus = deviceStatus;
    }

    public PerformanceVerificationMessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(PerformanceVerificationMessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public LiteLmHardware getHardware() {
        return hardware;
    }

    public void setHardware(LiteLmHardware hardware) {
        this.hardware = hardware;
    }

    public Instant getLastComm() {
        return lastComm;
    }

    public void setLastComm(Instant lastComm) {
        this.lastComm = lastComm;
    }

    public UnknownStatus getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(UnknownStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}
