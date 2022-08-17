package com.cannontech.common.dr.program.setup.model;

public class ProgramControlWindowFields {

    private Integer availableStartTimeInMinutes;

    private Integer availableStopTimeInMinutes;

    public ProgramControlWindowFields() {
    }

    public ProgramControlWindowFields(Integer availableStartTimeInMinutes, Integer availableStopTimeInMinutes) {
        this.availableStartTimeInMinutes = availableStartTimeInMinutes;
        this.availableStopTimeInMinutes = availableStopTimeInMinutes;
    }

    public Integer getAvailableStartTimeInMinutes() {
        return availableStartTimeInMinutes;
    }

    public Integer getAvailableStopTimeInMinutes() {
        return availableStopTimeInMinutes;
    }

    public void setAvailableStartTimeInMinutes(Integer availableStartTimeInMinutes) {
        this.availableStartTimeInMinutes = availableStartTimeInMinutes;
    }

    public void setAvailableStopTimeInMinutes(Integer availableStopTimeInMinutes) {
        this.availableStopTimeInMinutes = availableStopTimeInMinutes;
    }
    
}
