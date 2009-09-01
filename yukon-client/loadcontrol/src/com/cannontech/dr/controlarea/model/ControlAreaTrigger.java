package com.cannontech.dr.controlarea.model;

public class ControlAreaTrigger {
    public enum TriggerType {
        THRESHOLD("Threshold"), STATUS("Status");
        private final String dbString;

        private TriggerType(String dbString) {
            this.dbString = dbString;
        }

        public String getDbString() {
            return dbString;
        }
    }

    private Integer controlAreaId;
    private Integer triggerNumber;
    private TriggerType type;

    public ControlAreaTrigger(int controlAreaId, int triggerNumber, String type) {
        this.controlAreaId = controlAreaId;
        this.triggerNumber = triggerNumber;
        if (type != null && type.equalsIgnoreCase(TriggerType.THRESHOLD.getDbString())) {
            this.type = TriggerType.THRESHOLD;
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

}
