package com.cannontech.database.db.point.stategroup;

public enum TrueFalse implements PointState {
    FALSE(0),
    TRUE(1);
      
    private int rawState;
    
    private TrueFalse(int rawState) {
        this.rawState = rawState;
    }

    @Override
    public int getRawState() {
        return rawState;
    }


}
