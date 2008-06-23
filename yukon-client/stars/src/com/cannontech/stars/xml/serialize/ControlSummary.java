package com.cannontech.stars.xml.serialize;

public class ControlSummary {
    private int dailyTime;
    private boolean hasDailyTime;
    private int monthlyTime;
    private boolean hasMonthlyTime;
    private int seasonalTime;
    private boolean hasSeasonalTime;
    private int annualTime;
    private boolean hasAnnualTime;

    public ControlSummary() {

    }

    public int getAnnualTime() {
        return this.annualTime;
    } 

    public int getDailyTime() {
        return this.dailyTime;
    }

    public int getMonthlyTime() {
        return this.monthlyTime;
    } 

    public int getSeasonalTime() {
        return this.seasonalTime;
    } 

    public boolean hasAnnualTime() {
        return this.hasAnnualTime;
    } 

    public boolean hasDailyTime() {
        return this.hasDailyTime;
    }

    public boolean hasMonthlyTime() {
        return this.hasMonthlyTime;
    } 

    public boolean hasSeasonalTime() {
        return this.hasSeasonalTime;
    }

    public void setAnnualTime(int annualTime) {
        this.annualTime = annualTime;
        this.hasAnnualTime = true;
    }

    public void setDailyTime(int dailyTime) {
        this.dailyTime = dailyTime;
        this.hasDailyTime = true;
    }

    public void setMonthlyTime(int monthlyTime) {
        this.monthlyTime = monthlyTime;
        this.hasMonthlyTime = true;
    }

    public void setSeasonalTime(int seasonalTime) {
        this.seasonalTime = seasonalTime;
        this.hasSeasonalTime = true;
    }

}
