package com.cannontech.database.db.point.stategroup;

public enum Commissioned implements PointState {
    CONNECTED(0),
    DECOMMISSIONED(1),
    DISCONNECTED(2);
    
    private int rawState;
    
    private Commissioned(int rawState) {
        this.rawState = rawState;
    }

    @Override
    public int getRawState() {
        return rawState;
    }
    
    public static Commissioned getForRawState(int rawState) {
        
        for (Commissioned state : values() ) {
            if (state.getRawState() == rawState) {
                return state;
            }
        }
        
        throw new IllegalArgumentException();
    }
}
