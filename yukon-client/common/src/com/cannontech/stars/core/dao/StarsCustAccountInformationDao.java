package com.cannontech.stars.core.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.stars.database.data.lite.LiteAccountInfo;

public interface StarsCustAccountInformationDao {

    /**
     * This method returns the liteStarCustAccountInformation for the supplied accountId.
     */
    public LiteAccountInfo getByAccountId(int accountId);
    
    /**
     * This method does validation as well as getting the lite stars customer account information.
     *
     * @deprecated use the getByAccountId(int accountId) instead of this method.
     */
    @Deprecated
    public LiteAccountInfo getById(int accountId, int energyCompanyId);
    
    public Map<Integer, LiteAccountInfo> getByIds(Set<Integer> accountIds, int energyCompanyId);
    
    /**
     * @deprecated Refactor any code that would need to load all 
     *             LiteStarsCustAccountInformation from the database.
     * @param energyCompanyId
     * @return
     */
    @Deprecated 
    public List<LiteAccountInfo> getAll(int energyCompanyId);
    
}
