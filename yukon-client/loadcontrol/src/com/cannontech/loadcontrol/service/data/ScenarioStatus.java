package com.cannontech.loadcontrol.service.data;

import java.util.List;

public class ScenarioStatus {

    private String scenarioName;
    private List<ProgramStatus> programStatii;
    
    public ScenarioStatus(String scenarioName, List<ProgramStatus> programsStaii) {
        this.scenarioName = scenarioName;
        this.programStatii = programsStaii;
    }
    
    public String getScenarioName() {
        return scenarioName;
    }
    
    public List<ProgramStatus> getProgramStatii() {
        return programStatii;
    }
}
