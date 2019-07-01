package com.cannontech.dr.loadgroup.service;

import java.util.List;

import com.cannontech.common.dr.setup.ControlScenarioBase;
import com.cannontech.common.dr.setup.ControlScenarioProgram;

public interface ControlScenarioService {

    /**
     * Saves the Control Scenario.
     */
    int create(ControlScenarioBase controlScenario);

    /**
     * Retrieve Control Scenario for the controlScenarioId.
     */
    ControlScenarioBase retrieve(int controlScenarioId);

    /**
     * Update the Control Scenario for available controlScenarioId.
     */
    int update(int controlScenarioId, ControlScenarioBase controlScenario);

    /**
     * Delete the Control Scenario.
     */
    int delete(int controlScenarioId, String scenarioName);

    /**
     * Return all the Programs which belong to Control Area.
     */
    List<ControlScenarioProgram> getAvailablePrograms();

}
