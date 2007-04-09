package com.cannontech.web.cc;

import java.util.TimeZone;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.AdministratorRole;

public class CommercialCurtailmentBean {
    private LiteYukonUser yukonUser;
    private AuthDao authDao;

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
        LiteEnergyCompany energyCompany = DaoFactory.getEnergyCompanyDao().getEnergyCompany(getYukonUser());
        TimeZone timeZone = DaoFactory.getEnergyCompanyDao().getEnergyCompanyTimeZone(energyCompany);
        return timeZone;
    }
    
    public String getTimeFormat() {
        return "H:mm";
    }
    
    public boolean isAdminUser() {
        return authDao.checkRole(getYukonUser(), AdministratorRole.ROLEID);
    }
    
    public String getDateFormat() {
        return "M/d/yy";
    }
    
    public String getDateTimeFormat() {
        return "M/d/yy H:mm";
    }
    
    public LiteEnergyCompany getEnergyCompany() {
        return DaoFactory.getEnergyCompanyDao().getEnergyCompany(getYukonUser());
    }
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    

}
