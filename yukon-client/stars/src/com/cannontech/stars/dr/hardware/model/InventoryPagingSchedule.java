package com.cannontech.stars.dr.hardware.model;

import org.joda.time.ReadablePeriod;

public class InventoryPagingSchedule {
    private int inventoryPagingScheduleId;
    private String startTimeCronString;
    private ReadablePeriod period;
    private int pagingDelayInSeconds;
    private boolean enabled;

    public int getInventoryPagingScheduleId() {
        return inventoryPagingScheduleId;
    }

    public void setInventoryPagingScheduleId(int inventoryPagingScheduleId) {
        this.inventoryPagingScheduleId = inventoryPagingScheduleId;
    }

    public String getStartTimeCronString() {
        return startTimeCronString;
    }

    public void setStartTimeCronString(String startTimeCronString) {
        this.startTimeCronString = startTimeCronString;
    }

    public ReadablePeriod getPeriod() {
        return period;
    }

    public void setPeriod(ReadablePeriod period) {
        this.period = period;
    }

    public int getPagingDelayInSeconds() {
        return pagingDelayInSeconds;
    }

    public void setPagingDelayInSeconds(int pagingDelayInSeconds) {
        this.pagingDelayInSeconds = pagingDelayInSeconds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
