package com.cannontech.dr.meterDisconnect;

import java.util.List;

import org.joda.time.Instant;

/**
 * Data object describing a disconnect meter and it's control status in a DR event.
 */
public class DrMeterEventStatus {
    private int eventId;
    private int deviceId;
    private int inventoryId;
    private String meterDisplayName;
    private DrMeterControlStatus controlStatus;
    private Instant controlStatusTime;
    private DrMeterControlStatus restoreStatus;
    private Instant restoreStatusTime;
    
    private static final List<DrMeterControlStatus> canControlStatuses = List.of(
        DrMeterControlStatus.CONTROL_FAILED,
        DrMeterControlStatus.CONTROL_TIMEOUT,
        DrMeterControlStatus.CONTROL_UNKNOWN
    );
    
    private static final List<DrMeterControlStatus> canRestoreStatuses = List.of(
        DrMeterControlStatus.CONTROL_CONFIRMED,
        DrMeterControlStatus.RESTORE_FAILED,
        DrMeterControlStatus.RESTORE_TIMEOUT,
        DrMeterControlStatus.RESTORE_UNKNOWN
    );
    
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getMeterDisplayName() {
        return meterDisplayName;
    }

    public void setMeterDisplayName(String meterDisplayName) {
        this.meterDisplayName = meterDisplayName;
    }

    public DrMeterControlStatus getControlStatus() {
        return controlStatus;
    }

    public void setControlStatus(DrMeterControlStatus controlStatus) {
        this.controlStatus = controlStatus;
    }

    public Instant getControlStatusTime() {
        return controlStatusTime;
    }

    public void setControlStatusTime(Instant controlStatusTime) {
        this.controlStatusTime = controlStatusTime;
    }

    public DrMeterControlStatus getRestoreStatus() {
        return restoreStatus;
    }

    public void setRestoreStatus(DrMeterControlStatus restoreStatus) {
        this.restoreStatus = restoreStatus;
    }

    public Instant getRestoreStatusTime() {
        return restoreStatusTime;
    }

    public void setRestoreStatusTime(Instant restoreStatusTime) {
        this.restoreStatusTime = restoreStatusTime;
    }

    public boolean canSendControl() {
        return canControlStatuses.contains(controlStatus);
    }
    
    public boolean canSendRestore() {
        return canRestoreStatuses.contains(controlStatus);
    }
}
