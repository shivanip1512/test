package com.cannontech.dr.scenario.dao;

import java.util.List;

import com.cannontech.common.device.model.DisplayableDevice;

public interface ScenarioDao {
    public List<DisplayableDevice> getScenarios();
    public DisplayableDevice getScenario(int scenarioId);
}
