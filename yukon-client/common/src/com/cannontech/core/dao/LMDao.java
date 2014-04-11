package com.cannontech.core.dao;

import java.util.Set;

import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface LMDao {
    LiteYukonPAObject[] getAllLMScenarios();

    LiteLMProgScenario[] getLMScenarioProgs(int scenarioID);

    Set<LiteYukonPAObject> getAllLMDirectPrograms();

    int getStartingGearForScenarioAndProgram(int programId, int scenarioId);
}
