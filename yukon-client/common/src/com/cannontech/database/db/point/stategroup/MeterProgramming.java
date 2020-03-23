package com.cannontech.database.db.point.stategroup;

public enum MeterProgramming implements PointState {
    SUCCESS(0),
    FAILURE(1),
    ;

    private int rawState;

    private MeterProgramming(int rawState) {
        this.rawState = rawState;
    }

    @Override
    public int getRawState() {
        return rawState;
    }

}
