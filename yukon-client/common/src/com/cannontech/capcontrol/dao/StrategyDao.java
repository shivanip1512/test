package com.cannontech.capcontrol.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.capcontrol.model.StrategyLimitsHolder;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;

public interface StrategyDao {

    List<CapControlStrategy> getAllStrategies();

    /**
     * Returns a List<LiteCapControlStrategy>, lites only contain names and id's.
     * @return List<LiteCapControlStrategy>
     */
    List<LiteCapControlStrategy> getAllLiteStrategies();

    /**
     * Returns a List<LiteCapControlStrategy>, of any strategies without the specified control algorithms.  lites only contain names and id's.
     * @return List<LiteCapControlStrategy>
     */
    List<LiteCapControlStrategy> getLiteStrategiesWithoutSpecifiedAlgorithms(List<String> algorithms);

    int add(String name);

    boolean delete(int strategyId);
    
    void save(CapControlStrategy strategy);

    List<String> getAllPaoNamesUsingStrategyAssignment(int strategyId);

    CapControlStrategy getForId(int strategyId);

    StrategyLimitsHolder getStrategyLimitsHolder(int strategyId);

    /**
     * Checks if there is already a strategy of the same name and type
     * @return true if there is a conflict
     */
    boolean isUniqueName(String name);

    LiteCapControlStrategy getLiteStrategy(int id);

    /**
     * Returns a sorted {@link LinkedHashMap} of strategy id to {@link LiteCapControlStrategy}
     */
    Map<Integer, LiteCapControlStrategy> getLiteStrategies(Iterable<Integer> strategyIds);
    
}