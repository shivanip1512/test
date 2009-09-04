package com.cannontech.dr.controlarea.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.PaoIdentifier;

public class ControlArea extends DisplayableDevice {

    private List<ControlAreaTrigger> triggers;

    public ControlArea(PaoIdentifier paoIdentifier, String name) {
        super(paoIdentifier, name);
    }

    public List<ControlAreaTrigger> getTriggers() {
        if (triggers == null)
            return Collections.emptyList();
        return triggers;
    }

    public void setTriggers(List<ControlAreaTrigger> triggers) {
        this.triggers = triggers;
    }
}
