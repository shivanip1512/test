package com.cannontech.web.tdc;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.tdc.model.AltScanRateEnum;
import com.cannontech.common.tdc.model.EnableDisableEnum;
import com.cannontech.tags.Tag;

public class DisplayBackingBean {
    private int deviceId;
    private int pointId;
    private AltScanRateEnum altScanRate;
    private EnableDisableEnum pointEnableDisable = EnableDisableEnum.ENABLED;
    private EnableDisableEnum deviceEnableDisable = EnableDisableEnum.ENABLED;
    private Double value;
    private int stateId;
    private List<Tag> tags = new ArrayList<>();
    private boolean deviceControlInhibited;
    private int rowIndex;
        
    public AltScanRateEnum getAltScanRate() {
        return altScanRate;
    }

    public void setAltScanRate(AltScanRateEnum altScanRate) {
        this.altScanRate = altScanRate;
    }
    
    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public EnableDisableEnum getPointEnableDisable() {
        return pointEnableDisable;
    }

    public void setPointEnableDisable(EnableDisableEnum pointEnableDisable) {
        this.pointEnableDisable = pointEnableDisable;
    }

    public EnableDisableEnum getDeviceEnableDisable() {
        return deviceEnableDisable;
    }

    public void setDeviceEnableDisable(EnableDisableEnum deviceEnableDisable) {
        this.deviceEnableDisable = deviceEnableDisable;
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
}
