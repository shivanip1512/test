package com.cannontech.dr.scenario.dao;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;

public interface ScenarioDao {
    public List<DisplayablePao> getScenarios();
    public DisplayablePao getScenario(int scenarioId);
    public List<DisplayablePao> getScenariosForProgram(int programId);
}
