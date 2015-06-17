package com.cannontech.web.capcontrol.service;

import java.util.List;
import java.util.Map;

import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.model.Season;

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

    /** Return a map of season to strategy assignment. */
    Map<Season, LiteCapControlStrategy> getSeasonStrategyAssignments(int id);
    
}