package com.cannontech.capcontrol.model;

import com.cannontech.common.model.Phase;

public class ZoneAssignmentPointRow extends ZoneAssignmentRow {
    private Phase phase;
    private boolean ignore;
    
    public ZoneAssignmentPointRow() {
        super();
    }
    public Phase getPhase() {
        return phase;
    }
    public void setPhase(Phase phase) {
        this.phase = phase;
    }
    public boolean isIgnore() {
        return ignore;
    }
    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
    @Override
    public String toString() {
        return String.format(
            "ZoneAssignmentPointRow [phase=%s, isIgnore()=%s, getId()=%s, getName()=%s, getDevice()=%s, getGraphPositionOffset()=%s, getDistance()=%s, isDeletion()=%s]",
            phase, isIgnore(), getId(), getName(), getDevice(), getGraphPositionOffset(), getDistance(), isDeletion());
    }

}
