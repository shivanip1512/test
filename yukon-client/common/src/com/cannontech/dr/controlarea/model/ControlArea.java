package com.cannontech.dr.controlarea.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;

public class ControlArea extends DisplayablePaoBase {

    private List<ControlAreaTrigger> triggers;

    public ControlArea(PaoIdentifier paoIdentifier, String name) {
        super(paoIdentifier, name);
    }

    public List<ControlAreaTrigger> getTriggers() {
        if (triggers == null)
            return Collections.emptyList();
        return triggers;
    }

    public boolean isHasThresholdTrigger() {
        for (ControlAreaTrigger trigger : getTriggers()) {
            if (trigger.getType() == TriggerType.THRESHOLD || 
                trigger.getType() == TriggerType.THRESHOLD_POINT) {
                return true;
            }
        }
        return false;
    }

    public void setTriggers(List<ControlAreaTrigger> triggers) {
        this.triggers = triggers;
    }
}
