package com.cannontech.stars.dr.appliance.dao;

import java.util.List;

import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceType;

public interface ApplianceDao {

    public ApplianceType getApplianceType(int applianceId);
    
    public Appliance getById(int applianceId);
    
    public List<Appliance> getByAccountId(int accountId);
    
}
