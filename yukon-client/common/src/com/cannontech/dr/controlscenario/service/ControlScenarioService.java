package com.cannontech.dr.controlscenario.service;

import com.cannontech.common.dr.setup.ControlScenario;

public interface ControlScenarioService {

    /**
     * Create the Control Scenario.
     */
    int create(ControlScenario controlScenario);

    /**
     * Retrieve Control Scenario for the controlScenarioId.
     */
    ControlScenario retrieve(int controlScenarioId);

    /**
     * Update the Control Scenario for available controlScenarioId.
     */
    int update(int controlScenarioId, ControlScenario controlScenario);

    /**
     * Delete the Control Scenario.
     */
    int delete(int controlScenarioId, String scenarioName);

}
