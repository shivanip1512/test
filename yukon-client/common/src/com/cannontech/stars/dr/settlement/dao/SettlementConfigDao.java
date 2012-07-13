package com.cannontech.stars.dr.settlement.dao;

import com.cannontech.database.data.lite.LiteSettlementConfig;


public interface SettlementConfigDao {
    
    public void save(LiteSettlementConfig liteSettlementConfig, int availRateListEntryId);

    public void delete(LiteSettlementConfig liteSettlementConfig);
    
}