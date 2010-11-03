package com.cannontech.capcontrol;

public enum TapOperation {
    NONE(0,"none"),
    LOWER_TAP(1,"LowerTap"),
    RAISE_TAP(2,"RaiseTap");

    private int tapOperationId;
    private String displayValue;
    
    private TapOperation(int tapOperationId, String displayValue) {
        this.tapOperationId = tapOperationId;
        this.displayValue = displayValue;
    }
    
    public int getTapOperationId() {
        return tapOperationId;
    }
    
    public String getDisplayValue() {
        return displayValue;
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
