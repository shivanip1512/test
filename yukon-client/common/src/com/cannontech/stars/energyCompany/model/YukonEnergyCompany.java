package com.cannontech.stars.energyCompany.model;

import org.joda.time.DateTimeZone;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonEnergyCompany {
    
    public int getEnergyCompanyId();
    
    public String getName();
    
    public LiteYukonUser getEnergyCompanyUser();
    
    public DateTimeZone getDefaultDateTimeZone();
}
