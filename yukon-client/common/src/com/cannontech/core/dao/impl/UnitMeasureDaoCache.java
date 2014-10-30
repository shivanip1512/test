package com.cannontech.core.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.google.common.collect.Table;

/**
 * This is the first caching dao I've written. I don't expect it to be the 
 * last. Something more generic will have to be created if we do many more
 * of these, but that'll have to wait for 3.5.
 * 
 * Don't copy this!!!
 * 
 * --tmack
 */
public class UnitMeasureDaoCache implements UnitMeasureDao {
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

    @Override
    public LiteUnitMeasure getLiteUnitMeasure(int uomid) {
        LiteUnitMeasure measure = byIdCache.get(uomid);
        if (measure == null) {
            measure = delegate.getLiteUnitMeasure(uomid);
            byIdCache.put(uomid, measure);
        }
        return measure;
    }

    @Override
    public LiteUnitMeasure getLiteUnitMeasure(String uomName) {
        String uomNameToLower = uomName.toLowerCase();
        LiteUnitMeasure measure = byNameCache.get(uomNameToLower);
        return measure;
    }

    @Override
    public LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID) {
        return delegate.getLiteUnitMeasureByPointID(pointID);
    }

    @Override
    public List<LiteUnitMeasure> getLiteUnitMeasures() {
        return delegate.getLiteUnitMeasures();
    }

    @PostConstruct
    public void init() throws Exception {
        // suppress warning here for the sake of clean code elsewhere
        byNameCache = new HashMap<String, LiteUnitMeasure>();
        byIdCache = new HashMap<Integer, LiteUnitMeasure>();

        // Do an initial load of all the unitMeasure values
        List<LiteUnitMeasure> liteUnitMeasures = delegate.getLiteUnitMeasures();
        for (LiteUnitMeasure liteUnitMeasure : liteUnitMeasures) {
            String toLowerLongName = liteUnitMeasure.getLongName().toLowerCase();
            byNameCache.put(toLowerLongName, liteUnitMeasure);
            byIdCache.put(liteUnitMeasure.getUomID(), liteUnitMeasure);
        }
    }

    @Override
    public Table<Integer, PointIdentifier, LiteUnitMeasure> getLiteUnitMeasureByPaoIdAndPoint(List<? extends YukonPao> paos) {
        return delegate.getLiteUnitMeasureByPaoIdAndPoint(paos);
    }
}
