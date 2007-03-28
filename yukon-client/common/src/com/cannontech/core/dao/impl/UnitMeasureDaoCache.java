package com.cannontech.core.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.springframework.beans.factory.InitializingBean;

import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteUnitMeasure;

/**
 * This is the first caching dao I've written. I don't expect it to be the 
 * last. Something more generic will have to be created if we do many more
 * of these, but that'll have to wait for 3.5.
 * 
 * Don't copy this!!!
 * 
 * --tmack
 */
public class UnitMeasureDaoCache implements UnitMeasureDao, InitializingBean {
    private int cacheSize = 15;
    private UnitMeasureDao delegate;
    private Map<String, LiteUnitMeasure> byNameCache;
    private Map<Integer, LiteUnitMeasure> byIdCache;

    @SuppressWarnings("unchecked")
    public UnitMeasureDaoCache() {
    }
    
    public UnitMeasureDao getDelegate() {
        return delegate;
    }

    public void setDelegate(UnitMeasureDao delegate) {
        this.delegate = delegate;
    }

    public LiteUnitMeasure getLiteUnitMeasure(int uomid) {
        LiteUnitMeasure measure = byIdCache.get(uomid);
        if (measure == null) {
            measure = delegate.getLiteUnitMeasure(uomid);
            byIdCache.put(uomid, measure);
        }
        return measure;
    }

    public LiteUnitMeasure getLiteUnitMeasure(String uomName) {
        LiteUnitMeasure measure = byNameCache.get(uomName);
        if (measure == null) {
            measure = delegate.getLiteUnitMeasure(uomName);
            byNameCache.put(uomName, measure);
        }
        return measure;
    }

    public LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID) {
        return delegate.getLiteUnitMeasureByPointID(pointID);
    }

    public List<LiteUnitMeasure> getLiteUnitMeasures() {
        return delegate.getLiteUnitMeasures();
    }

    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        // suppress warning here for the sake of clean code elsewhere
        byNameCache = new LRUMap(cacheSize);
        byIdCache = new LRUMap(cacheSize);
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

}
