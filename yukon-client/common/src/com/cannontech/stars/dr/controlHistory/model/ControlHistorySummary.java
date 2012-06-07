package com.cannontech.stars.dr.controlHistory.model;

import org.joda.time.Duration;

import com.cannontech.stars.xml.serialize.StarsLMControlHistory;

public class ControlHistorySummary {
    private Duration dailySummary;
    private Duration monthlySummary;
    private Duration yearlySummary;
    
    public ControlHistorySummary(){}
    public ControlHistorySummary(StarsLMControlHistory starsLMControlHistory) {

        Duration dailySummary = starsLMControlHistory.getControlSummary().getDailyTime();
        this.dailySummary = dailySummary;
        
        Duration monthlySummary = starsLMControlHistory.getControlSummary().getMonthlyTime();
        this.monthlySummary = monthlySummary;
        
        Duration yearlySummary = starsLMControlHistory.getControlSummary().getAnnualTime();
        this.yearlySummary = yearlySummary;
    }
    
    public Duration getDailySummary() {
        return dailySummary;
    }
    public void setDailySummary(Duration dailySummary) {
        this.dailySummary = dailySummary;
    }
    
    public Duration getMonthlySummary() {
        return monthlySummary;
    }
    public void setMonthlySummary(Duration monthlySummary) {
        this.monthlySummary = monthlySummary;
    }
    
    public Duration getYearlySummary() {
        return yearlySummary;
    }
    public void setYearlySummary(Duration yearlySummary) {
        this.yearlySummary = yearlySummary;
    }
    
}
