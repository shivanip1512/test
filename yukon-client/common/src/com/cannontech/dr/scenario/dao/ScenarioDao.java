package com.cannontech.dr.scenario.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.google.common.collect.Multimap;

public interface ScenarioDao {
	
    public Scenario getScenario(int scenarioId);
    public List<Scenario> getAllScenarios();

    public List<Scenario> findScenariosForProgram(int programId);

    /**
     * Get a list of ScenarioPrograms for a given scenario keyed by program id.
     * @param scenarioId
     * @return a map of Integer programId to ScenarioProgram
     */
    public Map<Integer, ScenarioProgram> findScenarioProgramsForScenario(int scenarioId);
    
    /**
     * Returns the map of scenarioId to groupIds
     */
    Multimap<Integer, Integer> getGroupIdsByScenarioId(Set<Integer> scenarioIds);
}
