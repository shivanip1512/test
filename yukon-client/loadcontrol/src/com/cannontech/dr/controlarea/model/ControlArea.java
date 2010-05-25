package com.cannontech.dr.controlarea.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.model.DrActionStateEnum;

public class ControlArea extends ControllablePao {

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
            if (trigger.getType() == TriggerType.THRESHOLD) {
                return true;
            }
        }
        return false;
    }

    public void setTriggers(List<ControlAreaTrigger> triggers) {
        this.triggers = triggers;
    }

    @Override
    public DrActionStateEnum getDrActionState() {
        return DrActionStateEnum.CONTROLLABLE;
    }

}
