package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;

public interface LMProgramWebPublishingDao {

    public List<LiteLMProgramEvent> getProgramHistory(int accountId, List<Integer> energyCompanyIds);

    public List<LiteStarsLMProgram> getPrograms(LiteStarsCustAccountInformation account, List<Integer> energyCompanyIds);
    
    public List<LiteLMProgramWebPublishing> getAllWebPublishing(int energyCompanyId);
    
}
