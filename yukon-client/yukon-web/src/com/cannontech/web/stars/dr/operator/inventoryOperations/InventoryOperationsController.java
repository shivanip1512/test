package com.cannontech.web.stars.dr.operator.inventoryOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class InventoryOperationsController {
    
    private StarsDatabaseCache starsDatabaseCache;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    /* Home - Landing Page */
    @RequestMapping(value = "/operator/inventory/inventoryOperations/home", method = RequestMethod.GET)
    public String home(ModelMap modelMap, YukonUserContext userContext) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        modelMap.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyId());
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String title = messageSourceAccessor.getMessage("yukon.web.modules.operator.inventoryOperations.fileUploadTitle");
        modelMap.addAttribute("fileUploadTitle", title);
        
        return "operator/inventory/inventoryOperations/home.jsp";
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}