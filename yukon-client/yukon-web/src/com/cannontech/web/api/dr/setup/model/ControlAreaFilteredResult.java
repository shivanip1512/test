package com.cannontech.web.api.dr.setup.model;

import java.util.List;

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.database.YNBoolean;

public class ControlAreaFilteredResult {

    private LMDto controlArea;
    private List<LMDto> assignedPrograms;
    private YNBoolean trigger;

    public LMDto getControlArea() {
        return controlArea;
    }

    public void setControlArea(LMDto controlArea) {
        this.controlArea = controlArea;
    }

    public List<LMDto> getAssignedPrograms() {
        return assignedPrograms;
    }

    public void setAssignedPrograms(List<LMDto> assignedPrograms) {
        this.assignedPrograms = assignedPrograms;
    }

    public YNBoolean getTrigger() {
        return trigger;
    }

    public void setTrigger(YNBoolean trigger) {
        this.trigger = trigger;
    }

}
