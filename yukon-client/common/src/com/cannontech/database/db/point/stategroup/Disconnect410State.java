package com.cannontech.database.db.point.stategroup;

public enum Disconnect410State implements PointState {
    CONFIRMED_DISCONNECTED(0),
    CONNECTED(1),
    UNCONFIRMED_DISCONNECTED(2),
    CONNECT_ARMED(3),
    ;
    
    private final int rawState;
    
    private Disconnect410State(int rawState) {
        this.rawState = rawState;
    }

    @Override
    public int getRawState() {
        return rawState;
    }
}
