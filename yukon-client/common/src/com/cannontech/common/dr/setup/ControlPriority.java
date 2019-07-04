package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ControlPriority implements DisplayableEnum {
    DEFAULT(1),
    MEDIUM(2),
    HIGH(3),
    HIGHEST(4);
    
    private Integer controlPriorityValue;

    private ControlPriority(Integer controlPriorityValue) {
        this.controlPriorityValue = controlPriorityValue;
    }

    public Integer getControlPriorityValue() {
        return controlPriorityValue;
    }
    
    public static ControlPriority getDisplayValue(Integer value) {
        for (ControlPriority control : ControlPriority.values()) {
            if (control.getControlPriorityValue().equals(value)) {
                return control;
            }
        }
        return null;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }
}
