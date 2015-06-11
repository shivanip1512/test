package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.capcontrol.model.StrategyLimitsHolder;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;

public interface StrategyDao {

    public List<CapControlStrategy> getAllStrategies();

    /**
     * Returns a List<LiteCapControlStrategy>, lites only contain names and id's.
     * @return List<LiteCapControlStrategy>
     */
    public List<LiteCapControlStrategy> getAllLiteStrategies();

    public int add(String name);

    public boolean delete(int strategyId);
    
    public void save(CapControlStrategy strategy);

    public List<String> getAllPaoNamesUsingStrategyAssignment(int strategyId);

    public CapControlStrategy getForId(int strategyId);

    public StrategyLimitsHolder getStrategyLimitsHolder(int strategyId);

    /**
     * Checks if there is already a strategy of the same name and type
     * @return true if there is a conflict
     */
    public boolean isUniqueName(String name);
}
