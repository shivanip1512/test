package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.cannontech.common.util.Pair;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;

public abstract class AbstractFilter<E> {
    private LiteStarsEnergyCompany energyCompany;
    private Date startDate;
    private Date stopDate;

    protected abstract boolean doFilterCheck(E e, FilterWrapper filter);
    
    protected abstract Comparator<Integer> getFilterMapComparator();
    
    public <R> List<R> filter(final List<R> list, final List<FilterWrapper> filterList) {
        final Map<Integer,List<FilterWrapper>> andFilterMap = createFilterMap(filterList);
        final List<R> resultList = new ArrayList<R>();
        
        for (final R r : list) {
            final E e = this.extractObject(r);
            
            boolean andMatch = false;
            boolean failed = false;
            
            for (final Integer key : andFilterMap.keySet()) {
                final List<FilterWrapper> wrapperList = andFilterMap.get(key);
                andMatch = this.doOrCheck(wrapperList, e);
                if (!andMatch)  {
                    failed = true;
                    break;
                }
            }
            
            if (!failed) resultList.add(r);
        }
        
        return resultList;
    }
    
    protected boolean doOrCheck(final List<FilterWrapper> filterList, E e) {
        boolean orMatch = false;
        for (FilterWrapper filter : filterList) {
            orMatch = this.doFilterCheck(e, filter);
            if (orMatch) break;
        }
        return orMatch;
    }
    
    private Map<Integer,List<FilterWrapper>> createFilterMap(final List<FilterWrapper> filterList) {
        final Comparator<Integer> filterMapComparator = this.getFilterMapComparator();
        final Map<Integer,List<FilterWrapper>> map = new TreeMap<Integer,List<FilterWrapper>>(filterMapComparator);
        
        for (final FilterWrapper filter : filterList) {
            final Integer key = Integer.valueOf(filter.getFilterTypeID());
            List<FilterWrapper> filterOrList = map.get(key);
            if (filterOrList == null) {
                filterOrList = new ArrayList<FilterWrapper>();
                map.put(key, filterOrList);
            }
            filterOrList.add(filter);
        }
        return map;
    }
    
    @SuppressWarnings("unchecked")
    private E extractObject(final Object o) {
        E e;
        if (o instanceof Pair) {
            Pair p = (Pair) o;
            e = (E) p.getFirst();
        } else {
            e = (E) o;
        }
        return e;
    }

    public LiteStarsEnergyCompany getEnergyCompany() {
        return energyCompany;
    }

    public void setEnergyCompany(LiteStarsEnergyCompany energyCompany) {
        this.energyCompany = energyCompany;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

}
