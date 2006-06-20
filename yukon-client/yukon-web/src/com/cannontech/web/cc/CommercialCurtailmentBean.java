package com.cannontech.web.cc;

import java.util.TimeZone;

import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

public class CommercialCurtailmentBean {
    private LiteYukonUser yukonUser;

    public CommercialCurtailmentBean() {
        super();
    }

    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }
    
    public TimeZone getTimeZone() {
        LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany(getYukonUser());
        TimeZone timeZone = EnergyCompanyFuncs.getEnergyCompanyTimeZone(energyCompany);
        return timeZone;
    }
    
    public String getTimeFormat() {
        return "H:mm";
    }
    
    public String getDateFormat() {
        return "M/d/yy";
    }
    
    public String getDateTimeFormat() {
        return "M/d/yy H:mm";
    }
    
    public LiteEnergyCompany getEnergyCompany() {
        return EnergyCompanyFuncs.getEnergyCompany(getYukonUser());
    }
    

}
