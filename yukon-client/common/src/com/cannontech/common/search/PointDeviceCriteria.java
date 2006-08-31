package com.cannontech.common.search;

import org.apache.lucene.search.Query;


/**
 * Implementors of this class must provide a zero-argument constructor. The search
 * framework will instantiate a new instance so it must be ready to use after construction.
 */
public interface PointDeviceCriteria {
    public Query getCriteria();
    
}
