package com.cannontech.stars.dr.settlement.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.stars.dr.settlement.model.AvailableRate;
import com.cannontech.stars.dr.settlement.model.SettlementDto;

public interface SettlementService {
    
    /**
     * This method returns a list of all the editable settlement configurations of a given settlement,
     * which is represented by the yukonDefId.
     */
    public List<LiteSettlementConfig> getEditableConfigs(int yukonDefId);
    
    /**
     * This method returns all of the available rates for a given settlement, which is represented by the yukonDefId. 
     */
    public List<AvailableRate> getAvailableRates(int energyCompanyId, int yukonDefId);

    /**
     * 
     * @param settlementDto
     */
    public void saveSettlementDto(SettlementDto settlementDto, int energyCompanyId, int settlementYukonDefId);
    
}