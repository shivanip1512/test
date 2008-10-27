package com.cannontech.loadcontrol.service.data;

import java.util.List;

import org.springframework.core.style.ToStringCreator;

public class ScenarioProgramStartingGears {

    private String scenarioName;
    private List<ProgramStartingGear> programStartingGears;
    
    public ScenarioProgramStartingGears(String scenarioName, List<ProgramStartingGear> programStartingGears) {
        this.scenarioName = scenarioName;
        this.programStartingGears = programStartingGears;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public List<ProgramStartingGear> getProgramStartingGears() {
        return programStartingGears;
    }
    
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("scenarioName", getScenarioName());
        tsc.append("programStartingGears", getProgramStartingGears());
        return tsc.toString(); 
    }
}
