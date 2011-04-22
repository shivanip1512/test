package com.cannontech.database.db.point.stategroup;

public enum Commissioned implements PointState {
    COMMISSIONED(0),
    DECOMMISSIONED(1);
    
    private int rawState;
    
    private Commissioned(int rawState) {
        this.rawState = rawState;
    }

    @Override
    public int getRawState() {
        return rawState;
    }
}
