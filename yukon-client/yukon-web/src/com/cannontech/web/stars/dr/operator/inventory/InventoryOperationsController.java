package com.cannontech.web.stars.dr.operator.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class InventoryOperationsController {
    
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private RolePropertyDao rolePropertyDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    private ConfigurationSource configurationSource;
    
    /* Home - Landing Page */
    @RequestMapping(value = "/operator/inventory/inventoryOperations/home", method = RequestMethod.GET)
    public String home(ModelMap modelMap, YukonUserContext userContext) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_RECONFIG, userContext.getYukonUser());
        
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        modelMap.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyId());
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String title = messageSourceAccessor.getMessage("yukon.web.modules.operator.inventoryOperations.fileUploadTitle");
        modelMap.addAttribute("fileUploadTitle", title);
        
        boolean showLinks = configurationSource.getBoolean("DIGI_ENABLED", false);
        modelMap.addAttribute("showLinks", showLinks);
        return "operator/inventory/inventoryOperations/home.jsp";
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
}