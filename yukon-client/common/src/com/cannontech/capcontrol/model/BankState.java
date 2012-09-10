package com.cannontech.capcontrol.model;

import com.cannontech.database.db.point.stategroup.PointState;

public enum BankState implements PointState {
    OPEN(0),
    CLOSE(1),
    OPEN_QUESTIONABLE(2),
    CLOSE_QUESTIONABLE(3),
    OPEN_FAIL(4),
    CLOSE_FAIL(5),
    OPEN_PENDING(6),
    CLOSE_PENDING(7);

    private BankState(int rawState) {
        this.rawState = rawState;
    }

    private int rawState;

    @Override
    public int getRawState() {
        return rawState;
    }
    
}