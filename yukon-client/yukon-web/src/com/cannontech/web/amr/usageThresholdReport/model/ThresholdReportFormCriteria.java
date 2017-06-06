package com.cannontech.web.amr.usageThresholdReport.model;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;

public class ThresholdReportFormCriteria extends ThresholdReportCriteria {
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }

    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    private DeviceCollection deviceCollection;

}
