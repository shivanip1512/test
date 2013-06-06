package com.cannontech.web.cc;

import java.util.TimeZone;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;

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
        return authDao.getUserTimeZone(getYukonUser());
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
    
    public String getNotificationTimeFormat() {
        return "M/d/yy H:mm:ss";
    }
    
    public LiteEnergyCompany getEnergyCompany() {
        return YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(getYukonUser());
    }
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    

}
