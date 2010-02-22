package com.cannontech.stars.dr.controlhistory.model;

public class ControlHistorySummary {
    private int dailySummary;
    private int monthlySummary;
    private int yearlySummary;
    
    public int getDailySummary() {
        return dailySummary;
    }
    public int getDailySummaryMS() {
        return dailySummary*1000;
    }
    public void setDailySummary(int dailySummary) {
        this.dailySummary = dailySummary;
    }
    
    public int getMonthlySummary() {
        return monthlySummary;
    }
    public int getMonthlySummaryMS() {
        return monthlySummary*1000;
    }
    public void setMonthlySummary(int monthlySummary) {
        this.monthlySummary = monthlySummary;
    }
    
    public int getYearlySummary() {
        return yearlySummary;
    }
    public int getYearlySummaryMS() {
        return yearlySummary*1000;
    }
    public void setYearlySummary(int yearlySummary) {
        this.yearlySummary = yearlySummary;
    }
}
