package com.cannontech.common.dr.program.setup.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.device.lm.LMProgramControlWindow;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ProgramControlWindow implements DBPersistentConverter<LMProgramControlWindow> {

    private ProgramControlWindowFields controlWindowOne;
    private ProgramControlWindowFields controlWindowTwo;

    public ProgramControlWindowFields getControlWindowOne() {
        return controlWindowOne;
    }

    public void setControlWindowOne(ProgramControlWindowFields controlWindowOne) {
        this.controlWindowOne = controlWindowOne;
    }

    public ProgramControlWindowFields getControlWindowTwo() {
        return controlWindowTwo;
    }

    public void setControlWindowTwo(ProgramControlWindowFields controlWindowTwo) {
        this.controlWindowTwo = controlWindowTwo;
    }

    @Override
    public void buildModel(LMProgramControlWindow programControlWindow) {
        ProgramControlWindowFields fields = buildProgramControlWindowFields(programControlWindow);
        if (programControlWindow.getWindowNumber().intValue() == 1) {
            setControlWindowOne(fields);
        }

        if (programControlWindow.getWindowNumber().intValue() == 2) {
            setControlWindowTwo(fields);
        }
    }

    private ProgramControlWindowFields buildProgramControlWindowFields(LMProgramControlWindow window) {
        int localStartTime = window.getAvailableStartTime() / 60;
        int stopTime = window.getAvailableStopTime();
        if (stopTime > 86400) {
            stopTime = stopTime - 86400;
        }
        int localStopTime = stopTime / 60;
        ProgramControlWindowFields controlWindow = new ProgramControlWindowFields(localStartTime, localStopTime);
        return controlWindow;
    }

    @Override
    public void buildDBPersistent(LMProgramControlWindow programControlWindow) {
    }
}
