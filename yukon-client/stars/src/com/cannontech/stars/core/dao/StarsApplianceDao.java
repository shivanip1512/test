package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.stars.LiteStarsAppliance;

public interface StarsApplianceDao {

    public List<LiteStarsAppliance> getByAccountId(int accountId, int energyCompanyId);
    
    public List<LiteStarsAppliance> getUnassignedAppliances(int accountId, int energyCompanyId);

    public LiteStarsAppliance getByApplianceIdAndEnergyCompanyId(int applianceId, int energyCompanyId);    
    
}
