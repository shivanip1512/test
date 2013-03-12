package com.cannontech.amr.monitors.message;

import java.io.Serializable;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.worker.ServiceWorkerQueueObject;

public class DeviceDataMonitorMessage implements Serializable, ServiceWorkerQueueObject {
    private static final long serialVersionUID = 1L;

    private final DeviceDataMonitor updatedMonitor;
    private final DeviceDataMonitor oldMonitor;
    private final boolean isBeforeSave;

    public DeviceDataMonitorMessage(DeviceDataMonitor updatedMonitor) {
        this.updatedMonitor = updatedMonitor;
        this.oldMonitor = null;
        this.isBeforeSave = false;
    }

    public DeviceDataMonitorMessage(DeviceDataMonitor updatedMonitor, DeviceDataMonitor oldMonitor) {
        this.updatedMonitor = updatedMonitor;
        this.oldMonitor = oldMonitor;
        this.isBeforeSave = true;
    }

    public DeviceDataMonitor getUpdatedMonitor() {
        return updatedMonitor;
    }

    public DeviceDataMonitor getOldMonitor() {
        return oldMonitor;
    }

    public boolean isBeforeSave() {
        return isBeforeSave;
    }

    @Override
    public String toString() {
        return "DeviceDataMonitorMessage [updatedMonitor=" + updatedMonitor + ", oldMonitor="
               + oldMonitor + ", isBeforeSave=" + isBeforeSave + "]";
    }

    @Override
    public Integer getId() {
        return updatedMonitor.getId();
    }
}
