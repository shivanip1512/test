package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface LMDao {

    public LiteYukonPAObject[] getAllLMScenarios();

    public LiteLMProgScenario[] getLMScenarioProgs(int scenarioID);

}