package com.cannontech.stars.util.filter;

import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.stars.util.FilterWrapper;

public interface Filter<E> {

    public int getFilterCount(List<Integer> energyCompanyIdList, 
            List<FilterWrapper> filterWrapperList) throws PersistenceException;
    
    public int getFilterCount(List<Integer> energyCompanyIdList,
            List<FilterWrapper> filterWrapperList, Date startDate, Date stopDate) throws PersistenceException;

    /**
     * Executes the SQL query with the given Filters and calls on Processor to
     * process the results between the specified fromIndex inclusive, and
     * toIndex exclusive.
     * 
     * @param fromIndex inclusive
     * @param toIndex exclusive
     * @param processor
     * @param energyCompanyIdList
     * @param filterWrapperList
     * @param orderBy
     * @throws PersistenceException
     */
    public void filter(int fromIndex, int toIndex, Processor<E> processor,
            List<Integer> energyCompanyIdList, List<FilterWrapper> filterWrapperList,
                DirectionAwareOrderBy orderBy) throws PersistenceException;

    /**
     * Executes the SQL query with the given Filters and calls on Processor to
     * process the results between the specified fromIndex inclusive, and
     * toIndex exclusive.
     * 
     * @param fromIndex inclusive
     * @param toIndex exclusive
     * @param processor
     * @param energyCompanyIdList
     * @param filterWrapperList
     * @param orderBy
     * @throws PersistenceException
     */    
    public void filter(int fromIndex, int toIndex, Processor<E> processor, 
            List<Integer> energyCompanyIdList, List<FilterWrapper> filterWrapperList,
                DirectionAwareOrderBy orderBy, Date startDate, Date stopDate) throws PersistenceException;
    
}
