package com.cannontech.web.tools.device.config.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.device.model.DisplayableDevice;

public class DeviceConfigActionHistory {
    private DisplayableDevice device;
    private List<DeviceConfigActionHistoryDetail> details = new ArrayList<>();

    public List<DeviceConfigActionHistoryDetail> getDetails() {
        return details;
    }

    public void setDetails(List<DeviceConfigActionHistoryDetail> details) {
        this.details = details;
    }

    public DisplayableDevice getDevice() {
        return device;
    }

    public void setDevice(DisplayableDevice device) {
        this.device = device;
    }
}
