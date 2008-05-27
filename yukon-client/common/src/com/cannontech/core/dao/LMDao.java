package com.cannontech.core.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface LMDao {

    public LiteYukonPAObject[] getAllLMScenarios();
    
    public LiteYukonPAObject[] getAllMemberLMScenarios(LiteYukonUser user);

    public LiteLMProgScenario[] getLMScenarioProgs(int scenarioID);
    
    public Set<LiteYukonPAObject> getAllLMDirectPrograms();
    
    public List<Integer> getProgramsForControlArea(int areaID);
    
    public int getStartingGearForScenarioAndProgram(int programId, int scenarioId);

}