package com.cannontech.common.dr.program.setup.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.device.lm.LMProgramControlWindow;

public class ProgramControlWindowFields implements DBPersistentConverter<LMProgramControlWindow> {

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

    @Override
    public void buildModel(LMProgramControlWindow lmProgramControlWindow) {
        int localStartTime = lmProgramControlWindow.getAvailableStartTime() / 60;
        int stopTime = lmProgramControlWindow.getAvailableStopTime();
        if (stopTime > 86400) {
            stopTime = stopTime - 86400;
        }
        int localStopTime = stopTime / 60;
        setAvailableStartTimeInMinutes(localStartTime);
        setAvailableStopTimeInMinutes(localStopTime);
    }

    @Override
    public void buildDBPersistent(LMProgramControlWindow lmProgramControlWindow) {
        int startTimeInSeconds = getAvailableStartTimeInMinutes() * 60;
        int stopTimeInSeconds = getAvailableStopTimeInMinutes() * 60;
        if (stopTimeInSeconds < startTimeInSeconds) {
            // make sure server knows that this is the next day
            stopTimeInSeconds = stopTimeInSeconds + 86400;
        }

        lmProgramControlWindow.setAvailableStartTime(startTimeInSeconds);
        lmProgramControlWindow.setAvailableStopTime(stopTimeInSeconds);
    }
}
