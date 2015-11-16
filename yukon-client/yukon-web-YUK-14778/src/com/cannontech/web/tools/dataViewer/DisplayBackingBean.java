package com.cannontech.web.tools.dataViewer;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.tdc.model.AltScanRate;
import com.cannontech.common.util.EnabledStatus;
import com.cannontech.tags.Tag;

public class DisplayBackingBean {
    private int deviceId;
    private int pointId;
    private AltScanRate altScanRate;
    private EnabledStatus pointEnabledStatus = EnabledStatus.ENABLED;
    private EnabledStatus deviceEnabledStatus = EnabledStatus.ENABLED;
    private Double value;
    private int stateId;
    private List<Tag> tags = new ArrayList<>();
    private boolean deviceControlInhibited;
    private int rowIndex;
        
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

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
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
}
