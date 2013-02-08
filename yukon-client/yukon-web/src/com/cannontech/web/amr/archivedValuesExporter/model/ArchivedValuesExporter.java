package com.cannontech.web.amr.archivedValuesExporter.model;

import java.util.Date;

import com.cannontech.common.bulk.collection.device.DeviceCollection;

public class ArchivedValuesExporter {

    private DeviceCollection deviceCollection;
    private int formatId;
    private Date endDate;

    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public int getFormatId() {
        return formatId;
    }
    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }

    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}