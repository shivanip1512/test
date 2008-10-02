package com.cannontech.stars.util.filter.filterby;

import java.util.Collection;

import com.cannontech.stars.util.FilterWrapper;

public interface FilterByProducer {

    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter);
    
    public int getFilterType();
    
}
