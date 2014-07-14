package com.cannontech.capcontrol.model;

import com.cannontech.common.model.Phase;

public class ZoneAssignmentPointRow extends ZoneAssignmentRow {
    private Phase phase;
    
    public ZoneAssignmentPointRow() {
        super();
    }
    public Phase getPhase() {
        return phase;
    }
    public void setPhase(Phase phase) {
        this.phase = phase;
    }
}
