package com.cannontech.common.program.widget.model;

import org.joda.time.DateTime;

/**
 * This class is to populate gear data for program widget and detail page.
 */
public class GearData {

    private String gearName;
    private DateTime startDateTime;
    private DateTime stopDateTime;
    private boolean knownGoodStopDateTime;
    private boolean isStoppedOnSameDay;

    public String getGearName() {
        return gearName;
    }

    public void setGearName(String gearName) {
        this.gearName = gearName;
    }

    public DateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(DateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public DateTime getStopDateTime() {
        return stopDateTime;
    }

    public void setStopDateTime(DateTime stopDateTime) {
        this.stopDateTime = stopDateTime;
    }

    public boolean isKnownGoodStopDateTime() {
        return knownGoodStopDateTime;
    }

    public void setKnownGoodStopDateTime(boolean knownGoodStopDateTime) {
        this.knownGoodStopDateTime = knownGoodStopDateTime;
    }

    public boolean isStoppedOnSameDay() {
        return isStoppedOnSameDay;
    }

    public void setStoppedOnSameDay(boolean isStoppedOnSameDay) {
        this.isStoppedOnSameDay = isStoppedOnSameDay;
    }
}
