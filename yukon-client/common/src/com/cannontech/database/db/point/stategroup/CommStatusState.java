package com.cannontech.database.db.point.stategroup;

public enum CommStatusState implements PointState {
    ANY(-1),
    CONNECTED(0),
    DISCONNECTED(1);
    
    private int rawState;
    
    private CommStatusState(int rawState) {
        this.rawState = rawState;
    }

    @Override
    public int getRawState() {
        return rawState;
    }
}
