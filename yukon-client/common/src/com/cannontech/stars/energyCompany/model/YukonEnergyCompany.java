package com.cannontech.stars.energyCompany.model;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonEnergyCompany {
    
    public int getEnergyCompanyId();
    
    public String getName();
    
    public LiteYukonUser getEnergyCompanyUser();

    public boolean isDefault();
}