
package com.cannontech.web.capcontrol.models;

public class SeasonStrategyAssignment {
    
    private String seasonName;
    private int strategyId;
    
    public SeasonStrategyAssignment() {/* Default Constructor for Spring */}
    
    public SeasonStrategyAssignment(String seasonName, int strategyId) {
        this.seasonName = seasonName;
        this.strategyId = strategyId;
    }
    
    public String getSeasonName() {
        return seasonName;
    }
    
    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }
    
    public int getStrategyId() {
        return strategyId;
    }
    
    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }
    
    public static SeasonStrategyAssignment of(String seasonName, int strategyId) {
        return new SeasonStrategyAssignment(seasonName, strategyId);
    }
    
    @Override
    public String toString() {
        return String.format("SeasonStrategyAssignment [seasonName=%s, strategyId=%s]", seasonName, strategyId);
    }
    
}