package com.cannontech.stars.dr.appliance.dao;

import java.util.List;

import com.cannontech.stars.dr.appliance.model.Appliance;

public interface ApplianceDao {

    public void updateApplianceKW(int applianceId, float applianceKW);
    
    public void deleteAppliancesByAccountId(int accountId);
    
    public void deleteAppliancesByAccountIdAndInventoryId(int accountId, int inventoryId);
    
    public Appliance getById(int applianceId);
    
    public List<Appliance> getByAccountId(int accountId);
    
    public List<Appliance> getByAccountIdAndProgramIdAndInventoryId(int accountId, int programId, int inventoryId);

    public void deleteAppliancesForAccount(int accountId);

    public void deleteAppliance(Appliance appliance);

    public void deleteAppliance(int applianceId);
    
}
