package com.cannontech.common.search;


/**
 * Implementors of this class must provide a zero-argument constructor. The search
 * framework will instantiate a new instance so it must be ready to use after construction.
 */
public interface PointDeviceCriteria {
    /**
     * An empty array indicates that all unit of measures should be
     * returned.
     */
    public Integer[] getUnitOfMeasureIds();
    
    
//    public int[] paoType;
//    public int[] paoCategory;
//    public int[] paoClass;
//    public int[] pointTypes;
    
}
