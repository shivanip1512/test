package com.cannontech.common.dr.program.setup.model;

public class ProgramControlWindowFields {

    private Integer availableStartTime;
    private Integer availableStopTime;

    ProgramControlWindowFields() {

    }

    public ProgramControlWindowFields(Integer availableStartTime, Integer availableStopTime) {
        this.availableStartTime = availableStartTime;
        this.availableStopTime = availableStopTime;
    }

    public Integer getAvailableStartTime() {
        return availableStartTime;
    }

    public Integer getAvailableStopTime() {
        return availableStopTime;
    }

}
