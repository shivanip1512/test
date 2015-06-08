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
    @Override
    public String toString() {
        return String.format(
            "ZoneAssignmentPointRow [phase=%s, getId()=%s, getName()=%s, getDevice()=%s, getGraphPositionOffset()=%s, getDistance()=%s, isDeletion()=%s]",
            phase, getId(), getName(), getDevice(), getGraphPositionOffset(), getDistance(), isDeletion());
    }
}
