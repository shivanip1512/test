package com.cannontech.stars.dr.controlhistory.model;

import org.joda.time.ReadableDuration;

public class ControlHistorySummary {
    private ReadableDuration dailySummary;
    private ReadableDuration monthlySummary;
    private ReadableDuration yearlySummary;
    
    public ReadableDuration getDailySummary() {
        return dailySummary;
    }
    public void setDailySummary(ReadableDuration dailySummary) {
        this.dailySummary = dailySummary;
    }
    
    public ReadableDuration getMonthlySummary() {
        return monthlySummary;
    }
    public void setMonthlySummary(ReadableDuration monthlySummary) {
        this.monthlySummary = monthlySummary;
    }
    
    public ReadableDuration getYearlySummary() {
        return yearlySummary;
    }
    public void setYearlySummary(ReadableDuration yearlySummary) {
        this.yearlySummary = yearlySummary;
    }
    
}
