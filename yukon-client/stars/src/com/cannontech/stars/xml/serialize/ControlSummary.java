package com.cannontech.stars.xml.serialize;

import org.joda.time.Duration;

public class ControlSummary {
    private Duration dailyTime = Duration.ZERO;
    private boolean hasDailyTime;
    private Duration monthlyTime = Duration.ZERO;
    private boolean hasMonthlyTime;
    private Duration seasonalTime = Duration.ZERO;
    private boolean hasSeasonalTime;
    private Duration annualTime = Duration.ZERO;
    private boolean hasAnnualTime;

    public Duration getAnnualTime() {
        return this.annualTime;
    } 

    public Duration getDailyTime() {
        return this.dailyTime;
    }

    public Duration getMonthlyTime() {
        return this.monthlyTime;
    } 

    public Duration getSeasonalTime() {
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

    public void setAnnualTime(Duration annualTime) {
        this.annualTime = annualTime;
        this.hasAnnualTime = true;
    }

    public void setDailyTime(Duration dailyTime) {
        this.dailyTime = dailyTime;
        this.hasDailyTime = true;
    }

    public void setMonthlyTime(Duration monthlyTime) {
        this.monthlyTime = monthlyTime;
        this.hasMonthlyTime = true;
    }

    public void setSeasonalTime(Duration seasonalTime) {
        this.seasonalTime = seasonalTime;
        this.hasSeasonalTime = true;
    }

    public void subtractDuration(Duration controlDuration) {
        if(seasonalTime.isLongerThan(Duration.ZERO)) {
            seasonalTime = seasonalTime.minus(controlDuration);
        }
        if(annualTime.isLongerThan(Duration.ZERO)) {
            annualTime = annualTime.minus(controlDuration);
        }
        if(monthlyTime.isLongerThan(Duration.ZERO)) {
            monthlyTime = monthlyTime.minus(controlDuration);
        }
        if(dailyTime.isLongerThan(Duration.ZERO)) {
            dailyTime = dailyTime.minus(controlDuration);
        }
    }

}
