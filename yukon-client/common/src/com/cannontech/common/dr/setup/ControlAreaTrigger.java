package com.cannontech.common.dr.setup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = { "triggerId" }, allowGetters = true, ignoreUnknown = true)
public class ControlAreaTrigger {

    private Integer triggerId;
    private Integer triggerNumber;
    private ControlAreaTriggerType triggerType;
    private Integer triggerPointId;
    private Integer normalState;
    private Double threshold;
    private ControlAreaProjection controlAreaProjection;
    private Integer atku;
    private Double minRestoreOffset;
    private Integer peakPointId;
    private Integer thresholdPointId;

    public Integer getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Integer triggerId) {
        this.triggerId = triggerId;
    }

    public Integer getTriggerNumber() {
        return triggerNumber;
    }

    public void setTriggerNumber(Integer triggerNumber) {
        this.triggerNumber = triggerNumber;
    }

    public ControlAreaTriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(ControlAreaTriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public Integer getTriggerPointId() {
        return triggerPointId;
    }

    public void setTriggerPointId(Integer triggerPointId) {
        this.triggerPointId = triggerPointId;
    }

    public Integer getNormalState() {
        return normalState;
    }

    public void setNormalState(Integer normalState) {
        this.normalState = normalState;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public ControlAreaProjection getControlAreaProjection() {
        return controlAreaProjection;
    }

    public void setControlAreaProjection(ControlAreaProjection controlAreaProjection) {
        this.controlAreaProjection = controlAreaProjection;
    }

    public Integer getAtku() {
        return atku;
    }

    public void setAtku(Integer atku) {
        this.atku = atku;
    }

    public Double getMinRestoreOffset() {
        return minRestoreOffset;
    }

    public void setMinRestoreOffset(Double minRestoreOffset) {
        this.minRestoreOffset = minRestoreOffset;
    }

    public Integer getPeakPointId() {
        return peakPointId;
    }

    public void setPeakPointId(Integer peakPointId) {
        this.peakPointId = peakPointId;
    }

    public Integer getThresholdPointId() {
        return thresholdPointId;
    }

    public void setThresholdPointId(Integer thresholdPointId) {
        this.thresholdPointId = thresholdPointId;
    }

}
