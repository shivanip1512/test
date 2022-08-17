package com.cannontech.common.dr.program.setup.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ProgramControlWindow {

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

}
