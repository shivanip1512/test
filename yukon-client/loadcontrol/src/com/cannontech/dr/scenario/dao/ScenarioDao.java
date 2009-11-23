package com.cannontech.dr.scenario.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.dr.scenario.model.ScenarioProgram;

public interface ScenarioDao {
	
    public DisplayablePao getScenario(int scenarioId);
    public List<DisplayablePao> getAllScenarios();

    public List<DisplayablePao> findScenariosForProgram(int programId);

    /**
     * Get a list of ScenarioPrograms for a given scenario keyed by program id.
     * @param scenarioId
     * @return a map of Integer programId to ScenarioProgram
     */
    public Map<Integer, ScenarioProgram> findScenarioProgramsForScenario(
            int scenarioId);
}
