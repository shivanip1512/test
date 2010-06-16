package com.cannontech.stars.dr.controlhistory.model;

import org.joda.time.Duration;

public class ControlHistorySummary {
    private Duration dailySummary;
    private Duration monthlySummary;
    private Duration yearlySummary;
    
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
