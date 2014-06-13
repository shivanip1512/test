package com.cannontech.web.stars.dr.operator.inventory;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@RequestMapping("/operator/inventory/uploadFile")
@CheckRole(YukonRole.INVENTORY)
public class FileUploadCollectionHelper {
    
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @RequestMapping("")
    public String fileUpload(HttpServletRequest request, ModelMap modelMap, FlashScope flashScope, YukonUserContext userContext) throws ServletRequestBindingException {
        
        try {
            InventoryCollection yukonCollection = inventoryCollectionFactory.createCollection(request);
            modelMap.addAllAttributes(yukonCollection.getCollectionParameters());
        } catch (CollectionCreationException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.home." + e.getMessage()));
            
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
            modelMap.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyId());
            
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String title = messageSourceAccessor.getMessage("yukon.web.modules.operator.inventory.home.fileUploadTitle");
            modelMap.addAttribute("fileUploadTitle", title);
            
            return "operator/inventory/home.jsp";
        }
        
        return "redirect:inventoryActions";
    }
}