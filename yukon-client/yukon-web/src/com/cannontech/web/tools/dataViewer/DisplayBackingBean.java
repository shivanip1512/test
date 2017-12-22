package com.cannontech.web.tools.dataViewer;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.cannontech.common.tdc.model.AltScanRate;
import com.cannontech.common.util.EnabledStatus;
import com.cannontech.tags.Tag;
import com.cannontech.web.tools.points.PointBackingBean;

public class DisplayBackingBean extends PointBackingBean {
    private int deviceId;
    private int displayId;
    private AltScanRate altScanRate;
    private EnabledStatus pointEnabledStatus = EnabledStatus.ENABLED;
    private EnabledStatus deviceEnabledStatus = EnabledStatus.ENABLED;
    private List<Tag> tags = new ArrayList<>();
    private boolean deviceControlInhibited;
    private int rowIndex;
    private String displayName;
    private DateTime date;
    
    public AltScanRate getAltScanRate() {
        return altScanRate;
    }

    public void setAltScanRate(AltScanRate altScanRate) {
        this.altScanRate = altScanRate;
    }
    
    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public boolean isDeviceControlInhibited() {
        return deviceControlInhibited;
    }

    public void setDeviceControlInhibited(boolean deviceControlInhibited) {
        this.deviceControlInhibited = deviceControlInhibited;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public EnabledStatus getPointEnabledStatus() {
        return pointEnabledStatus;
    }

    public void setPointEnabledStatus(EnabledStatus pointEnabledStatus) {
        this.pointEnabledStatus = pointEnabledStatus;
    }

    public EnabledStatus getDeviceEnabledStatus() {
        return deviceEnabledStatus;
    }

    public void setDeviceEnabledStatus(EnabledStatus deviceEnabledStatus) {
        this.deviceEnabledStatus = deviceEnabledStatus;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getDisplayId() {
        return displayId;
    }

    public void setDisplayId(int displayId) {
        this.displayId = displayId;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }
}
