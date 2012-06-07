package com.cannontech.stars.web.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.DirectionAwareOrderBy;
import com.cannontech.stars.util.filter.Filter;

public class SimpleCollectionFactory {
    private Filter<LiteInventoryBase> inventoryFilter;
    private Filter<LiteWorkOrderBase> workOrderFilter;
    
    public SimpleCollection<LiteInventoryBase> createInventorySeachCollection(List<LiteInventoryBase> resultList) {
        return new SearchBasedSimpleCollection<LiteInventoryBase>(resultList);
    }
    
    public SimpleCollection<LiteWorkOrderBase> createWorkOrderSearchCollection(List<LiteWorkOrderBase> resultList) {
        return new SearchBasedSimpleCollection<LiteWorkOrderBase>(resultList);
    }
    
    public SimpleCollection<LiteInventoryBase> createInventoryFilterCollection(
        List<Integer> energyCompanyIds, List<FilterWrapper> filterWrapperList,
            DirectionAwareOrderBy orderBy) {
        return new FilterBasedSimpleCollection<LiteInventoryBase>(inventoryFilter,
                                                                  energyCompanyIds,
                                                                  filterWrapperList,
                                                                  orderBy);
    }
    
    public SimpleCollection<LiteWorkOrderBase> createWorkOrderFilterCollection(
        List<Integer> energyCompanyIds, List<FilterWrapper> filterWrapperList,
            DirectionAwareOrderBy orderBy, Date startDate, Date stopDate) {
        return new DateRangeFilterBasedSimpleCollection<LiteWorkOrderBase>(workOrderFilter,
                                                                           energyCompanyIds,
                                                                           filterWrapperList,
                                                                           orderBy,
                                                                           startDate,
                                                                           stopDate);
    }
    
    private class DateRangeFilterBasedSimpleCollection<E> extends FilterBasedSimpleCollection<E> {
        private final Date startDate;
        private final Date stopDate;
        
        public DateRangeFilterBasedSimpleCollection(Filter<E> filter, 
                List<Integer> energyCompanyIds, List<FilterWrapper> filterWrapperList,
                DirectionAwareOrderBy orderBy, Date startDate, Date stopDate) {
            super(filter, energyCompanyIds, filterWrapperList, orderBy);
            this.startDate = startDate;
            this.stopDate = stopDate;
        }
        
        @Override
        public int getCount() throws PersistenceException {
            return filter.getFilterCount(energyCompanyIds, filterWrapperList, startDate, stopDate);
        }
        
        @Override
        public List<E> getList(int fromIndex, int toIndex) throws PersistenceException {
            CollectingProcessor<E> collectingProcessor = 
                new CollectingProcessor<E>();
            
            filter.filter(fromIndex,
                          toIndex,
                          collectingProcessor,
                          energyCompanyIds,
                          filterWrapperList,
                          orderBy,
                          startDate,
                          stopDate);
            
            List<E> list = collectingProcessor.getCollectedList();
            return list;    
        }
        
    }
    
    private class FilterBasedSimpleCollection<E> implements SimpleCollection<E> {
        protected final Filter<E> filter;
        protected final List<Integer> energyCompanyIds;
        protected final List<FilterWrapper> filterWrapperList;
        protected final DirectionAwareOrderBy orderBy;
        
        public FilterBasedSimpleCollection(Filter<E> filter, 
                List<Integer> energyCompanyIds, List<FilterWrapper> filterWrapperList,
                    DirectionAwareOrderBy orderBy) {
            this.filter = filter;
            this.energyCompanyIds = energyCompanyIds;
            this.filterWrapperList = filterWrapperList;
            this.orderBy = orderBy;
        }

        @Override
        public int getCount() throws PersistenceException {
            return filter.getFilterCount(energyCompanyIds, filterWrapperList);
        }

        @Override
        public List<E> getList() throws PersistenceException {
            return getList(0, Integer.MAX_VALUE);
        }

        @Override
        public List<E> getList(int fromIndex, int toIndex) throws PersistenceException {
            CollectingProcessor<E> collectingProcessor = 
                new CollectingProcessor<E>();
            
            filter.filter(fromIndex,
                                   toIndex,
                                   collectingProcessor,
                                   energyCompanyIds,
                                   filterWrapperList,
                                   orderBy);
            
            List<E> list = collectingProcessor.getCollectedList();
            return list;
        }

        @Override
        public Iterator<E> iterator() throws PersistenceException {
            return getList().iterator();
        }
    }
    
    private class SearchBasedSimpleCollection<E> implements SimpleCollection<E> {
        private final List<E> resultList;
        
        public SearchBasedSimpleCollection(List<E> resultList) {
            this.resultList = resultList;
        }
        
        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public List<E> getList() {
            return new ArrayList<E>(resultList);
        }

        @Override
        public List<E> getList(int fromIndex, int toIndex) {
            List<E> subList = resultList.subList(fromIndex,
                                                 getRealToIndex(resultList, toIndex));
            return new ArrayList<E>(subList);
        }

        @Override
        public Iterator<E> iterator() {
            return resultList.iterator();
        }
        
    }
    
    private class CollectingProcessor<T> implements Processor<T> {
        private final List<T> list = new ArrayList<T>();
        
        public List<T> getCollectedList() {
            return list;
        }
        
        @Override
        public void process(T object) throws ProcessingException {
            list.add(object);
        }

        @Override
        public void process(Collection<T> objectCollection)
                throws ProcessingException {
            list.addAll(objectCollection);
        }
    }
    
    private <R> int getRealToIndex(Collection<R> collection, int toIndex) {
        int size = collection.size();
        int realSize = (toIndex > size) ? size : toIndex;
        return realSize;
    }
    
    @Autowired
    public void setInventoryFilter(@Qualifier("inventoryFilter") Filter<LiteInventoryBase> inventoryFilter) {
        this.inventoryFilter = inventoryFilter;
    }
    
    @Autowired
    public void setWorkOrderFilter(@Qualifier("workOrderFilter") Filter<LiteWorkOrderBase> workOrderFilter) {
        this.workOrderFilter = workOrderFilter;
    }
    
}
