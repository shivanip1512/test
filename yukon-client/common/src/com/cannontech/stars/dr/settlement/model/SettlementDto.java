package com.cannontech.stars.dr.settlement.model;

import java.util.List;

import com.cannontech.database.data.lite.LiteSettlementConfig;

public class SettlementDto {

    private List<LiteSettlementConfig> editableLiteSettlementConfigs;
    private List<AvailableRate> availableRates;

    public List<LiteSettlementConfig> getEditableLiteSettlementConfigs() {
        return editableLiteSettlementConfigs;
    }
    public void setEditableLiteSettlementConfigs(List<LiteSettlementConfig> editableLiteSettlementConfigs) {
        this.editableLiteSettlementConfigs = editableLiteSettlementConfigs;
    }
    
    public List<AvailableRate> getAvailableRates() {
        return availableRates;
    }
    public void setAvailableRates(List<AvailableRate> availableRates) {
        this.availableRates = availableRates;
    }

}