package com.cannontech.dr.scenario.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.dr.scenario.model.ScenarioProgram;

public interface ScenarioDao {
    public DisplayablePao getScenario(int scenarioId);

    public List<DisplayablePao> findScenariosForProgram(int programId);

    public Map<Integer, ScenarioProgram> findScenarioProgramsForScenario(
            int scenarioId);
}
