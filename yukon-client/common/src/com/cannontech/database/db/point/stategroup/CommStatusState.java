package com.cannontech.database.db.point.stategroup;

public enum CommStatusState implements PointState {
    ANY(-1),
    CONNECTED(0),
    DISCONNECTED(1);
    
    private int rawState;
    
    private CommStatusState(int rawState) {
        this.rawState = rawState;
    }

    public static CommStatusState getForRawState(int rawState) {
        for (CommStatusState state : values()) {
            if (state.getRawState() == rawState) {
                return state;
            }
        }
        throw new IllegalArgumentException("No CommStatusState with rawState: " + rawState);
    }
    
    @Override
    public int getRawState() {
        return rawState;
    }
}
