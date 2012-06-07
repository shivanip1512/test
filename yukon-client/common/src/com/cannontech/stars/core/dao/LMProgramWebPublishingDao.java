package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.stars.database.data.lite.LiteLMProgramEvent;
import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMProgram;

public interface LMProgramWebPublishingDao {

    public List<LiteLMProgramEvent> getProgramHistory(int accountId, LiteStarsEnergyCompany energyCompany);

    public List<LiteStarsLMProgram> getPrograms(LiteStarsCustAccountInformation account, LiteStarsEnergyCompany energyCompany);
    
}
