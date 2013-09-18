package com.cannontech.database.db.point.stategroup;

public enum ThreeStateStatusState implements PointState {
    ANY(-1),
    OPEN(0),
    CLOSED(1),
    UNKNOWN(2),
    ;
    
    private int rawState;
    
    private ThreeStateStatusState(int rawState) {
        this.rawState = rawState;
    }

    @Override
    public int getRawState() {
        return rawState;
    }
}
