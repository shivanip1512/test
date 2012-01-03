package com.cannontech.database.db.point.stategroup;

public enum OutageStatus implements PointState {
    GOOD(0),
    QUESTIONABLE(1),
    BAD(2);

    private int rawState;

    private OutageStatus(int rawState) {
        this.rawState = rawState;
    }

    @Override
    public int getRawState() {
        return rawState;
    }

}
