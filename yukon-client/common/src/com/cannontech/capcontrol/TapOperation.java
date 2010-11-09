package com.cannontech.capcontrol;

public enum TapOperation {
    NONE(0),
    LOWER_TAP(1),
    RAISE_TAP(2);

    private int tapOperationId;
    
    private TapOperation(int tapOperationId) {
        this.tapOperationId = tapOperationId;
    }
    
    public int getTapOperationId() {
        return tapOperationId;
    }
    
    static public TapOperation getForTapOperationId(int tapOperationId) {
        
        for (TapOperation operation : TapOperation.values()) {
            if (operation.getTapOperationId() == tapOperationId) {
                return operation;
            }
        }
        return NONE;
    }
}