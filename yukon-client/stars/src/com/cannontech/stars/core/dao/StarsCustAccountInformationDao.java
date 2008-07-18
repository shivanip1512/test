package com.cannontech.stars.core.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;

public interface StarsCustAccountInformationDao {

    public LiteStarsCustAccountInformation getById(int accountId, int energyCompanyId);
    
    public Map<Integer, LiteStarsCustAccountInformation> getByIds(Set<Integer> accountIds, int energyCompanyId);
    
    public List<LiteStarsCustAccountInformation> getAll(int energyCompanyId);
    
}
