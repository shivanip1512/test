package com.cannontech.dr.controlarea.model;

public class ControlAreaTrigger {
    private Integer controlAreaId;
    private Integer triggerNumber;
    private TriggerType type;

    public ControlAreaTrigger(int controlAreaId, int triggerNumber, String type) {
        this.controlAreaId = controlAreaId;
        this.triggerNumber = triggerNumber;
        if (type != null && type.equalsIgnoreCase(TriggerType.THRESHOLD.getDbString())) {
            this.type = TriggerType.THRESHOLD;
        } else if (type != null && type.equalsIgnoreCase(TriggerType.THRESHOLD_POINT.getDbString())) {
            this.type = TriggerType.THRESHOLD_POINT;
        } else {
            this.type = TriggerType.STATUS;
        }
    }

    public Integer getControlAreaId() {
        return controlAreaId;
    }

    public Integer getTriggerNumber() {
        return triggerNumber;
    }

    public TriggerType getType() {
        return type;
    }

    public boolean isThresholdType() {
        return type.equals(TriggerType.THRESHOLD);
    }
    
    public boolean isThresholdPointType() {
        return type.equals(TriggerType.THRESHOLD_POINT);
    }


}
