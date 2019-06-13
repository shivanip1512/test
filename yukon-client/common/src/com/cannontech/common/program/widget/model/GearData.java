package com.cannontech.common.program.widget.model;

import org.joda.time.DateTime;

/**
 * This class is to populate gear data for program widget and detail page.
 */
public class GearData {

    private String gearName;
    private DateTime startDateTime;
    private DateTime stopDateTime;
    private DateTime eventTime;

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

    public DateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(DateTime eventTime) {
        this.eventTime = eventTime;
    }
}
