package com.cannontech.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.user.YukonUserContext;

@Controller
public class SystemAdminController {

    private RolePropertyDao rolePropertyDao;
    private EnergyCompanyDao energyCompanyDao;
    
    /* System Administration Pages */
    @RequestMapping("/systemAdmin")
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        boolean hasConfigEC = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_CONFIG_ENERGY_COMPANY, userContext.getYukonUser());
        boolean hasMultiSpeak = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP, userContext.getYukonUser());
        boolean hasUserGroupEditor = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_LM_USER_ASSIGN, userContext.getYukonUser());
        
        if (hasConfigEC) {
            return "redirect:/spring/adminSetup/energyCompany/home";
        }
        /* TODO */
        return "TODO";
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
    
}