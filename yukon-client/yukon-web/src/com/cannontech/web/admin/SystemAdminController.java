package com.cannontech.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.exception.NotAuthorizedException;
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
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        LiteYukonUser user = userContext.getYukonUser();
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user);
        boolean isEcOperator = energyCompanyService.isOperator(user);
        boolean hasMultiSpeak = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP, user);
        boolean hasUserGroupEditor = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_LM_USER_ASSIGN, user);
        
        if (superUser || isEcOperator) {
            return "redirect:/spring/adminSetup/energyCompany/home";
        } else if (hasMultiSpeak) {
            return "redirect:/spring/multispeak/setup/home";
        } else if (hasUserGroupEditor) {
            return "redirect:/spring/adminSetup/userGroupEditor/home";
        } else {
            throw new NotAuthorizedException("User " + user.getUsername() + "is not authorized to perform system administration");
        }
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