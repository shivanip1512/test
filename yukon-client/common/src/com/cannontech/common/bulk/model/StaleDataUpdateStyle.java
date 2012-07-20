package com.cannontech.common.bulk.model;

/**
 * Styles of data update for point import.
 */
public enum StaleDataUpdateStyle {
    ALWAYS(1),
    ON_CHANGE(2);
    
    private int index;
    
    private StaleDataUpdateStyle(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
}
