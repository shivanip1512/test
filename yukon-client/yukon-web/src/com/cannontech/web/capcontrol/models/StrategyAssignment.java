
package com.cannontech.web.capcontrol.models;

import java.util.List;

import com.cannontech.common.util.LazyList;

public class StrategyAssignment {
    
    private int paoId;
    private int seasonSchedule;
    private List<SeasonStrategyAssignment> seasonAssignments = LazyList.ofInstance(SeasonStrategyAssignment.class);
    private int holidaySchedule;
    private int holidayStrategy;
    
    public int getPaoId() {
        return paoId;
    }
    
    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
    
    public int getSeasonSchedule() {
        return seasonSchedule;
    }
    
    public void setSeasonSchedule(int seasonSchedule) {
        this.seasonSchedule = seasonSchedule;
        
    }
    public List<SeasonStrategyAssignment> getSeasonAssignments() {
        return seasonAssignments;
        
    }
    public void setSeasonAssignments(List<SeasonStrategyAssignment> seasonAssignments) {
        this.seasonAssignments = seasonAssignments;
    }
    
    public int getHolidaySchedule() {
        return holidaySchedule;
    }
    
    public void setHolidaySchedule(int holidaySchedule) {
        this.holidaySchedule = holidaySchedule;
    }
    
    public int getHolidayStrategy() {
        return holidayStrategy;
    }
    public void setHolidayStrategy(int holidayStrategy) {
        this.holidayStrategy = holidayStrategy;
    }
    
    @Override
    public String toString() {
        return String.format("StrategyAssignment [paoId=%s, seasonSchedule=%s, seasonAssignments=%s, " + 
                "holidaySchedule=%s, holidayStrategy=%s]", 
                paoId, seasonSchedule, seasonAssignments, holidaySchedule, holidayStrategy);
    }
    
}