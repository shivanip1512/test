package com.cannontech.stars.dr.settlement.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.stars.dr.settlement.model.AvailableRate;
import com.cannontech.stars.dr.settlement.model.SettlementDto;

public interface SettlementService {
    
    /**
     * This method returns a list of all the editable settlement configurations of a given settlement (HECO specifically).
     * Editable configurations include all except Demand Charge.
     */
    public List<LiteSettlementConfig> getEditableConfigs();
    
    /**
     * This method returns all of the available rates for the HECO settlement. 
     */
    public List<AvailableRate> getAvailableRates(int energyCompanyId);

    /**
     * 
     * @param settlementDto
     */
    public void saveSettlementDto(SettlementDto settlementDto, int energyCompanyId);
    
}