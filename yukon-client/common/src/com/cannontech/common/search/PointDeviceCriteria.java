package com.cannontech.common.search;

import java.util.Map;


/**
 * Implementors of this class must provide a zero-argument constructor. The search
 * framework will instantiate a new instance so it must be ready to use after construction.
 */
public interface PointDeviceCriteria {
    /**
     * An empty array indicates that all unit of measures should be
     * returned.
     */
    /**
     * contains the map of rules for a given criteria
     * keyed of the term name
     */
    public Map getRulesMap();
//    public int[] paoType;
//    public int[] paoCategory;
//    public int[] paoClass;
//    public int[] pointTypes;
    
}
