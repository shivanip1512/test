package com.cannontech.cc.service;

import java.util.Set;

import com.cannontech.cc.dao.BaseEventDao;

public abstract class StrategyGroupBase {
    private BaseEventDao baseEventDao;
    private Set<String> strategyKeys;
    private String requiredPointGroup;

    public StrategyGroupBase() {
        super();
    }
    
    public String getRequiredPointGroup() {
        return requiredPointGroup;
    }
    
    public void setRequiredPointGroup(String requiredPointGroup) {
        this.requiredPointGroup = requiredPointGroup;
    }
    
    /**
     * Currently injected by Spring for all extending classes.
     * @param strategyKeys
     */
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
