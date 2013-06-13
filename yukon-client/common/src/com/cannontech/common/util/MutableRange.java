package com.cannontech.common.util;



public class MutableRange<T extends Comparable<? super T>> extends BaseRange<T> implements ReadableRange<T> {    
    /**
     * Create an unbounded range.
     */
    public MutableRange() {
    }

    /**
     * Create a range from min to max, inclusive at both ends
     * @param min The minimum value. A null value represents no minimum.
     * @param max the maximum value. A null value represents no maximum.
     */     
    public MutableRange(T min, T max) {
        super(min, max);
    }
    
    public MutableRange(T min, boolean includesMinValue, T max, boolean includesMaxValue) {
        super(min, includesMinValue, max, includesMaxValue);
    }
    
    public void setMin(T minValue) {
        this.min = minValue;
    }
    
    public void setMax(T maxValue) {
        this.max = maxValue;
    }
    
    public void setIncludesMinValue(boolean includesMinValue) {
        this.includesMinValue = includesMinValue;
    }
    
    public void setIncludesMaxValue(boolean includesMaxValue) {
        this.includesMaxValue = includesMaxValue;
    }

}
