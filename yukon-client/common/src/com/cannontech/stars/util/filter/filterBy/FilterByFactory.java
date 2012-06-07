package com.cannontech.stars.util.filter.filterBy;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import com.cannontech.stars.util.FilterWrapper;

public class FilterByFactory implements InitializingBean {
    private List<FilterByProducer> filterByProducers;
    private Map<Integer, FilterByProducer> producerMap;
    
    public Collection<FilterBy> createFilterBys(List<FilterWrapper> filterWrapperList) {
        Set<FilterBy> filterBys = new HashSet<FilterBy>();

        for (final FilterWrapper wrapper : filterWrapperList) {
            Integer filterType = Integer.parseInt(wrapper.getFilterTypeID());
            FilterByProducer producer = producerMap.get(filterType);
            filterBys.addAll(producer.createFilterBys(wrapper));
        }

        return filterBys;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<Integer, FilterByProducer> map = new HashMap<Integer, FilterByProducer>(filterByProducers.size());

        for (final FilterByProducer factory : filterByProducers) {
            map.put(factory.getFilterType(), factory);
        }
        
        producerMap = map;
    }
    
    public void setFilterByProducers(List<FilterByProducer> filterByProducers) {
        this.filterByProducers = filterByProducers;
    }
    
}
