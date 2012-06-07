package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.stars.database.data.lite.LiteStarsAppliance;

public interface StarsApplianceDao {

    public List<LiteStarsAppliance> getByAccountId(int accountId, int energyCompanyId);
    
    public List<LiteStarsAppliance> getUnassignedAppliances(int accountId, int energyCompanyId);

    public LiteStarsAppliance getByApplianceIdAndEnergyCompanyId(int applianceId, int energyCompanyId);    
    
}
