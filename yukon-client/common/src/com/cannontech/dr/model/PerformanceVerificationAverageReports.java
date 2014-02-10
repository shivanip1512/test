package com.cannontech.dr.model;

public final class PerformanceVerificationAverageReports {

    private final PerformanceVerificationEventStats lastDay;
    private final PerformanceVerificationEventStats lastTwoDays;
    private final PerformanceVerificationEventStats lastSevenDays;
    private final PerformanceVerificationEventStats lastThirtyDays;

    public PerformanceVerificationAverageReports(PerformanceVerificationEventStats lastDay, 
                                                 PerformanceVerificationEventStats lastTwoDays, 
                                                 PerformanceVerificationEventStats lastSevenDays, 
                                                 PerformanceVerificationEventStats lastThirtyDays) {
        this.lastDay = lastDay;
        this.lastTwoDays = lastTwoDays;
        this.lastSevenDays = lastSevenDays;
        this.lastThirtyDays = lastThirtyDays;
    }

    public PerformanceVerificationEventStats getLastDay() {
        return lastDay;
    }

    public PerformanceVerificationEventStats getLastTwoDays() {
        return lastTwoDays;
    }

    public PerformanceVerificationEventStats getLastSevenDays() {
        return lastSevenDays;
    }

    public PerformanceVerificationEventStats getLastThirtyDays() {
        return lastThirtyDays;
    }

}
