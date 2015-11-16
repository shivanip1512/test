package com.cannontech.web.capcontrol.models;

public class HolidayStrategyAssignment {
    
    private int paoId;
    private int scheduleId;
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
    
    public int getStrategyId() {
        return strategyId;
    }
    
    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }
    
    @Override
    public String toString() {
        return String.format("HolidayStrategyAssignment [paoId=%s, scheduleId=%s, strategyId=%s]", paoId, scheduleId, strategyId);
    }
    
}