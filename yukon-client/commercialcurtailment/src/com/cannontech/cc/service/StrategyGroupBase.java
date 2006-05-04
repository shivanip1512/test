package com.cannontech.cc.service;

import java.util.Set;

import com.cannontech.cc.dao.BaseEventDao;

public abstract class StrategyGroupBase {
    private BaseEventDao baseEventDao;
    private Set<String> strategyKeys;

    public StrategyGroupBase() {
        super();
    }
    
    public abstract String getRequiredPointGroup();
    
    public void setStrategyKeys(Set<String> strategyKeys) {
        this.strategyKeys = strategyKeys;
    }
    
    public Set<String> getStrategyKeys() {
        return strategyKeys;
    }

    public BaseEventDao getBaseEventDao() {
        return baseEventDao;
    }

    public void setBaseEventDao(BaseEventDao baseEventDao) {
        this.baseEventDao = baseEventDao;
    }
    
}
