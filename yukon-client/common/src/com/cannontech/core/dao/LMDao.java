package com.cannontech.core.dao;

import java.util.Set;

import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface LMDao {

    public LiteYukonPAObject[] getAllLMScenarios();

    public LiteLMProgScenario[] getLMScenarioProgs(int scenarioID);
    
    public Set<LiteYukonPAObject> getAllLMDirectPrograms();

    public int getStartingGearForScenarioAndProgram(int programId, int scenarioId);

}