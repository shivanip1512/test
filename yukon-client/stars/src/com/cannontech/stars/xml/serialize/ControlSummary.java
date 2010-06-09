package com.cannontech.stars.xml.serialize;

import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

public class ControlSummary {
    private ReadableDuration dailyTime = Duration.ZERO;
    private boolean hasDailyTime;
    private ReadableDuration monthlyTime = Duration.ZERO;
    private boolean hasMonthlyTime;
    private ReadableDuration seasonalTime = Duration.ZERO;
    private boolean hasSeasonalTime;
    private ReadableDuration annualTime = Duration.ZERO;
    private boolean hasAnnualTime;

    public ReadableDuration getAnnualTime() {
        return this.annualTime;
    } 

    public ReadableDuration getDailyTime() {
        return this.dailyTime;
    }

    public ReadableDuration getMonthlyTime() {
        return this.monthlyTime;
    } 

    public ReadableDuration getSeasonalTime() {
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

    public void setAnnualTime(ReadableDuration annualTime) {
        this.annualTime = annualTime;
        this.hasAnnualTime = true;
    }

    public void setDailyTime(ReadableDuration dailyTime) {
        this.dailyTime = dailyTime;
        this.hasDailyTime = true;
    }

    public void setMonthlyTime(ReadableDuration monthlyTime) {
        this.monthlyTime = monthlyTime;
        this.hasMonthlyTime = true;
    }

    public void setSeasonalTime(ReadableDuration seasonalTime) {
        this.seasonalTime = seasonalTime;
        this.hasSeasonalTime = true;
    }

}
