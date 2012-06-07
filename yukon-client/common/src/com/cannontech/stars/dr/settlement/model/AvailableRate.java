package com.cannontech.stars.dr.settlement.model;

import java.util.List;

import com.cannontech.database.data.lite.LiteSettlementConfig;

public class AvailableRate {
    private int entryId;
    private boolean enabled;
    private String availableRateName;
    private List<LiteSettlementConfig> rateConfigurations;
    
    public AvailableRate(){}
    public AvailableRate(int entryId, String avaiableRateName, List<LiteSettlementConfig> rateConfigurations, boolean enabled) {
        this.entryId = entryId;
        this.availableRateName = avaiableRateName;
        this.rateConfigurations = rateConfigurations;
        this.enabled = enabled;
    }
    
    public int getEntryId() {
        return entryId;
    }
    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAvailableRateName() {
        return availableRateName;
    }
    public void setAvailableRateName(String availableRateName) {
        this.availableRateName = availableRateName;
    }
    
    public List<LiteSettlementConfig> getRateConfigurations() {
        return rateConfigurations;
    }
    public void setRateConfigurations(List<LiteSettlementConfig> rateConfigurations) {
        this.rateConfigurations = rateConfigurations;
    }
    
}