package com.cannontech.web.api.dr.setup.model;

import java.util.List;

import com.cannontech.common.dr.setup.LMDto;

public class ControlScenariosFilteredResult {

    private LMDto scenario;
    private List<LMDto> assignedPrograms;

    public LMDto getScenario() {
        return scenario;
    }

    public void setScenario(LMDto scenario) {
        this.scenario = scenario;
    }

    public List<LMDto> getAssignedPrograms() {
        return assignedPrograms;
    }

    public void setAssignedPrograms(List<LMDto> assignedPrograms) {
        this.assignedPrograms = assignedPrograms;
    }

}
