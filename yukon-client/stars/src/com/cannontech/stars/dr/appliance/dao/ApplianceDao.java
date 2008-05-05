package com.cannontech.stars.dr.appliance.dao;

import java.util.List;

import com.cannontech.stars.dr.appliance.model.Appliance;

public interface ApplianceDao {

    public Appliance getById(int applianceId);
    
    public List<Appliance> getByAccountId(int accountId);
    
}
