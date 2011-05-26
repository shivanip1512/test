package com.cannontech.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.user.YukonUserContext;

@Controller
public class SystemAdminController {

    private RolePropertyDao rolePropertyDao;
    private EnergyCompanyService energyCompanyService;
    
    /* System Administration Pages */
    @RequestMapping("/systemAdmin")
    public String home(YukonUserContext userContext, ModelMap model) {
        LiteYukonUser user = userContext.getYukonUser();
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user);
        boolean isEcOperator = energyCompanyService.isOperator(user);
        model.addAttribute("displayLinkToEnergyCompanySetup", (isEcOperator || superUser));
        return "home.jsp";
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
}