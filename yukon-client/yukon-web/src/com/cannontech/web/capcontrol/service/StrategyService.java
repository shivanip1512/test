package com.cannontech.web.capcontrol.service;

import java.util.List;

import com.cannontech.database.db.capcontrol.CapControlStrategy;

public interface StrategyService {
    

    /**
     * Add or Update a Strategy
     * Sends appropriate DB Change message
     */
    int save(CapControlStrategy strategy);
    
    /**
     * Remove a Strategy
     * Sends appropriate DB Change message
     */
    void delete(int strategyId);
    
    /**
     * Get a list of Pao Names that have the strategy assigned to them
     */
    List<String> getAllPaoNamesUsingStrategyAssignment(int strategyId);
    
}
