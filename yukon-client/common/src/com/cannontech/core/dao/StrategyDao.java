package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.db.capcontrol.CapControlStrategy;

public interface StrategyDao {

    public List<CapControlStrategy> getAllStrategies();

    public boolean delete(int strategyId);

    public boolean deleteStrategyAssignmentsByStrategyId(int strategyId);
    
    public int add(String name);
}
