package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;

public interface LMProgramWebPublishingDao {

    public List<LiteLMProgramEvent> getProgramHistory(int accountId, LiteStarsEnergyCompany energyCompany);

    public List<LiteStarsLMProgram> getPrograms(LiteStarsCustAccountInformation account, LiteStarsEnergyCompany energyCompany);
    
}
