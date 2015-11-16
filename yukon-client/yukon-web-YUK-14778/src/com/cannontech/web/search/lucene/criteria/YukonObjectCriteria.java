package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.Query;

/**
 * Implementors of this class must provide a zero-argument constructor. The search
 * framework will instantiate a new instance so it must be ready to use after construction.
 */
public interface YukonObjectCriteria {
    
    public Query getCriteria();
    
}