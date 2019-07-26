package com.cannontech.core.dao;

import java.util.Set;

import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface LMDao {
    LiteYukonPAObject[] getAllLMScenarios();

    LiteLMProgScenario[] getLMScenarioProgs(int scenarioID);

    // I renamed this because I am not brave enough to change it and I don't want anyone to ever use it.
    Set<LiteYukonPAObject> getAllProgramsForCommercialCurtailment();

    int getStartingGearForScenarioAndProgram(int programId, int scenarioId);
}
