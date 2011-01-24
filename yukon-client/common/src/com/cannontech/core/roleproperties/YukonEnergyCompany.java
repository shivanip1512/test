package com.cannontech.core.roleproperties;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonEnergyCompany {
    
    public int getEnergyCompanyId();
    
    public String getName();
    
    public LiteYukonUser getEnergyCompanyUser();
    
}