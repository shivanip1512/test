package com.cannontech.common.dr.setup;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ControlAreaTrigger implements DBPersistentConverter<LMControlAreaTrigger> {

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
    private String triggerPointName;

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
        if (minRestoreOffset != null) {
            this.minRestoreOffset = new BigDecimal(minRestoreOffset).setScale(4, RoundingMode.HALF_DOWN).doubleValue();
        } else {
            this.minRestoreOffset = minRestoreOffset;
        }
    }

    public Integer getPeakPointId() {
        return peakPointId != null && peakPointId == 0 ? null : peakPointId;
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

    public String getTriggerPointName() {
        return triggerPointName;
    }

    public void setTriggerPointName(String triggerPointName) {
        this.triggerPointName = triggerPointName;
    }

    /**
     * Build trigger model.
     */
    @Override
    public void buildModel(LMControlAreaTrigger areaTrigger) {
        setTriggerId(areaTrigger.getTriggerID());
        setTriggerType(ControlAreaTriggerType.getTriggerValue(areaTrigger.getTriggerType()));
        setTriggerPointId(areaTrigger.getPointID());

        if (areaTrigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_STATUS)) {
            setNormalState(areaTrigger.getNormalState());
        } else {
            setMinRestoreOffset(areaTrigger.getMinRestoreOffset());
            setPeakPointId(areaTrigger.getPeakPointID());
            if ((areaTrigger.getTriggerType()).equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD_POINT)) {
                setThresholdPointId(areaTrigger.getThresholdPointID());
            } else {
                setThreshold(areaTrigger.getThreshold());
                ControlAreaProjection projection = new ControlAreaProjection();
                projection.setProjectionType(
                        ControlAreaProjectionType.getProjectionValue(areaTrigger.getProjectionType()));
                projection.setProjectAheadDuration(areaTrigger.getProjectAheadDuration());
                projection.setProjectionPoint(areaTrigger.getProjectionPoints());
                setControlAreaProjection(projection);
                setAtku(areaTrigger.getThresholdKickPercent());
            }
        }
    }

    /**
     * Build DBPersistent for trigger.
     */
    @Override
    public void buildDBPersistent(LMControlAreaTrigger lmControlAreaTrigger) {
        lmControlAreaTrigger.setPointID(getTriggerPointId());
        lmControlAreaTrigger.setTriggerType(getTriggerType().getTriggerTypeValue());
        buildTriggerByType(lmControlAreaTrigger);
    }

    /**
     * Build LMControlAreaTrigger object for trigger type.
     */
    private void buildTriggerByType(LMControlAreaTrigger lmControlAreaTrigger) {

        if (getTriggerType().getTriggerTypeValue().equalsIgnoreCase(IlmDefines.TYPE_STATUS)) {
            lmControlAreaTrigger.setNormalState(getNormalState());
            lmControlAreaTrigger.setThreshold(0.0);
        } else {
            lmControlAreaTrigger.setNormalState(IlmDefines.INVALID_INT_VALUE);
            if ((getTriggerType().getTriggerTypeValue()).equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD_POINT)) {
                lmControlAreaTrigger.setThreshold(0.0);
                lmControlAreaTrigger.setThresholdPointID(getThresholdPointId());
            } else {
                lmControlAreaTrigger.setThreshold(getThreshold());
                if (getAtku() != null) {
                    lmControlAreaTrigger.setThresholdKickPercent(getAtku());
                }
                String projectionType = getControlAreaProjection().getProjectionType().getProjectionTypeValue();
                lmControlAreaTrigger.setProjectionType(projectionType);
                if (projectionType.equals(ControlAreaProjectionType.NONE.getDatabaseRepresentation())) {
                    lmControlAreaTrigger.setProjectionPoints(5);
                    lmControlAreaTrigger.setProjectAheadDuration(TimeIntervals.MINUTES_5.getSeconds());
                } else {
                    lmControlAreaTrigger.setProjectionPoints(getControlAreaProjection().getProjectionPoint());
                    lmControlAreaTrigger.setProjectAheadDuration(getControlAreaProjection().getProjectAheadDuration());
                }
            }

            if (getMinRestoreOffset() != null) {
                lmControlAreaTrigger.setMinRestoreOffset(getMinRestoreOffset());
            }

            if (getPeakPointId() != null) {
                lmControlAreaTrigger.setPeakPointID(getPeakPointId());
            } else {
                lmControlAreaTrigger.setPeakPointID(IlmDefines.INVALID_INT_VALUE);
            }
        }
    }
}
