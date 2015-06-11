package com.cannontech.web.capcontrol.models;

public class SeasonStrategyAssignment {
    
    private int paoId;
    private int scheduleId;
    private String season;
    private int strategyId;
    
    public int getPaoId() {
        return paoId;
    }
    
    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
    
    public int getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public String getSeason() {
        return season;
    }
    
    public void setSeason(String season) {
        this.season = season;
    }
    
    public int getStrategyId() {
        return strategyId;
    }
    
    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }
    
    @Override
    public String toString() {
        return String.format("SeasonStrategyAssignment [paoId=%s, scheduleId=%s, season=%s, strategyId=%s]", paoId, scheduleId, season, strategyId);
    }
    
}