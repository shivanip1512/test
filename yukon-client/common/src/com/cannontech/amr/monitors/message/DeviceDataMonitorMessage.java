package com.cannontech.amr.monitors.message;

import java.io.Serializable;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;

public class DeviceDataMonitorMessage implements Serializable{
    private static final long serialVersionUID = 1L;

    public static enum Action {
        CREATE, UPDATE, DISABLE, ENABLE, RECALCULATE
    }

    private final DeviceDataMonitor updatedMonitor;
    private final DeviceDataMonitor oldMonitor;
    private final Action action;

    public DeviceDataMonitorMessage(DeviceDataMonitor updatedMonitor, Action action) {
        this(updatedMonitor, null, action);
    }

    public DeviceDataMonitorMessage(DeviceDataMonitor updatedMonitor, DeviceDataMonitor oldMonitor, Action action) {
        this.updatedMonitor = updatedMonitor;
        this.oldMonitor = oldMonitor;
        this.action = action;
    }

    public DeviceDataMonitor getUpdatedMonitor() {
        return updatedMonitor;
    }

    public DeviceDataMonitor getOldMonitor() {
        return oldMonitor;
    }

    @Override
    public String toString() {
        return "DeviceDataMonitorMessage [action=" + action + " updatedMonitor=" + updatedMonitor + ", oldMonitor="
            + oldMonitor + "]";
    }

    public Integer getId() {
        return updatedMonitor.getId();
    }

    public Action getAction() {
        return action;
    }
}
