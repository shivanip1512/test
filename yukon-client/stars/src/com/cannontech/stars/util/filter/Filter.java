package com.cannontech.stars.util.filter;

import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.stars.util.FilterWrapper;

public interface Filter<E> {

    public int getFilterCount(List<Integer> energyCompanyIdList, 
            List<FilterWrapper> filterWrapperList);
    
    public int getFilterCount(List<Integer> energyCompanyIdList,
            List<FilterWrapper> filterWrapperList, Date startDate, Date stopDate);
    
    public void filter(int fromIndex, int toIndex, Processor<E> processor,
            List<Integer> energyCompanyIdList, List<FilterWrapper> filterWrapperList,
                DirectionAwareOrderBy orderBy);

    public void filter(int fromIndex, int toIndex, Processor<E> processor, 
            List<Integer> energyCompanyIdList, List<FilterWrapper> filterWrapperList,
                DirectionAwareOrderBy orderBy, Date startDate, Date stopDate);
    
}
