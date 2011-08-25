package com.cannontech.cbc.cyme.impl;

import com.cannontech.database.db.point.stategroup.PointState;

public class PointStateHelper {
    public enum BankState implements PointState {
        Open(0),
        Close(1),
        OpenQuestionable(2),
        CloseQuestionable(3),
        OpenFail(4),
        CloseFail(5),
        OpenPending(6),
        ClosePending(7);

        private BankState(int rawState) {
            this.rawState = rawState;
        }

        private int rawState;

        @Override
        public int getRawState() {
            return rawState;
        }

    }

}
