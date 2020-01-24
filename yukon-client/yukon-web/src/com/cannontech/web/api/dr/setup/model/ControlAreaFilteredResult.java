package com.cannontech.web.api.dr.setup.model;

import java.util.List;

import com.cannontech.common.dr.setup.ControlAreaTrigger;
import com.cannontech.common.dr.setup.LMDto;

public class ControlAreaFilteredResult {

    private Integer controlAreaId;
    private String controlAreaName;
    private List<LMDto> assignedPrograms;
    private List<ControlAreaTrigger> triggers;

    public Integer getControlAreaId() {
        return controlAreaId;
    }

    public void setControlAreaId(Integer controlAreaId) {
        this.controlAreaId = controlAreaId;
    }

    public String getControlAreaName() {
        return controlAreaName;
    }

    public void setControlAreaName(String controlAreaName) {
        this.controlAreaName = controlAreaName;
    }

    public List<LMDto> getAssignedPrograms() {
        return assignedPrograms;
    }

    public void setAssignedPrograms(List<LMDto> assignedPrograms) {
        this.assignedPrograms = assignedPrograms;
    }

    public List<ControlAreaTrigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<ControlAreaTrigger> triggers) {
        this.triggers = triggers;
    }

}
