package com.cannontech.database.db.point.stategroup;

public enum EventStatus implements PointState {
    CLEARED(0),
    ACTIVE(1),
    ;

    private int rawState;

    private EventStatus(int rawState) {
        this.rawState = rawState;
    }

    @Override
    public int getRawState() {
        return rawState;
    }

}
