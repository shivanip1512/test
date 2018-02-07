package com.cannontech.database.db.point.stategroup;

public enum LcrDeviceStatus implements PointState {
    UNKNOWN(0),
    IN_SERVICE(1),
    OUT_OF_SERVICE(2),
    TEMPORARILY_OUT_OF_SERVICE(3);

    private int rawState;

    @Override
    public int getRawState() {
        return rawState;
    }

    private LcrDeviceStatus(int rawState) {
        this.rawState = rawState;
    }

}
