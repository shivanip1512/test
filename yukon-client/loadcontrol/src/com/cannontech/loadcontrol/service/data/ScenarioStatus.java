package com.cannontech.loadcontrol.service.data;

import java.util.List;

public class ScenarioStatus {

    private String scenarioName;
    private List<ProgramStatus> programStatuses;
    
    public ScenarioStatus(String scenarioName, List<ProgramStatus> programStatuses) {
        this.scenarioName = scenarioName;
        this.programStatuses = programStatuses;
    }
    
    public String getScenarioName() {
        return scenarioName;
    }
    
    public List<ProgramStatus> getProgramStatuses() {
        return programStatuses;
    }
}
