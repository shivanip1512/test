package com.cannontech.web.cc;

import java.util.TimeZone;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

public class CommercialCurtailmentBean {
    private LiteYukonUser yukonUser;
    private AuthDao authDao;
    private RolePropertyDao rolePropertyDao;

    public CommercialCurtailmentBean() {
        super();
        rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
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
        return rolePropertyDao.checkRole(YukonRole.OPERATOR_ADMINISTRATOR, this.yukonUser);
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
    
    public EnergyCompany getEnergyCompany() {
        return YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(getYukonUser());
    }
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    

}
