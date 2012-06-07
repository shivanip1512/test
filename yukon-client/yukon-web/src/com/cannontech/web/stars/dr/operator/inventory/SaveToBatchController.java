package com.cannontech.web.stars.dr.operator.inventory;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.SaveToBatchHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.SaveToBatchHelper.SaveToBatchTask;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/operator/inventory/saveToBatch/*")
public class SaveToBatchController {
    
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private SaveToBatchHelper helper;
    @Autowired private PaoDao paoDao;
    private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    @RequestMapping
    public String setup(HttpServletRequest request, ModelMap model, String taskId, YukonUserContext userContext) throws ServletRequestBindingException, RemoteException {
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        LiteStarsEnergyCompany liteStarsEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompany);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        InventoryCollection inventoryCollection = (InventoryCollection) model.get("inventoryCollection");
    
        Boolean isUniformHardwareConfigType = true;
        InventoryIdentifier firstInventoryIdentifier = inventoryCollection.iterator().next();
        HardwareConfigType firstHardwareConfigType = firstInventoryIdentifier.getHardwareType().getHardwareConfigType();
        for (InventoryIdentifier inventory : inventoryCollection) {
            HardwareConfigType nextHardwareConfigType = inventory.getHardwareType().getHardwareConfigType();
            if (nextHardwareConfigType != firstHardwareConfigType) {
                isUniformHardwareConfigType = false;
                break;
            }
        }
        model.addAttribute("uniformHardwareConfigType", isUniformHardwareConfigType);
        
        String ecDefaultRoute;
        int ecDefaultRouteId = -1;    // All routeId's must be positive.  If this value persists   
        try {                         // beyond the try block below, then there is no default route.
            ecDefaultRouteId = liteStarsEnergyCompany.getDefaultRouteId();
            ecDefaultRoute = paoDao.getYukonPAOName(ecDefaultRouteId);
            ecDefaultRoute = accessor.getMessage("yukon.web.modules.operator.hardware.defaultRoute") + ecDefaultRoute;
        } catch(NotFoundException e) {
            ecDefaultRoute = accessor.getMessage("yukon.web.modules.operator.hardware.defaultRouteNone");
        }
        List<LiteYukonPAObject> routes = liteStarsEnergyCompany.getAllRoutes();
        model.addAttribute("routes", routes);
        model.addAttribute("ecDefaultRoute", ecDefaultRoute);
   
        SaveToBatchInfo saveToBatchInfo = new SaveToBatchInfo();
        saveToBatchInfo.setUseRoutes("current");
        saveToBatchInfo.setUseGroups("current");
        saveToBatchInfo.setEcDefaultRoute(ecDefaultRouteId);
        model.addAttribute("saveToBatchInfo", saveToBatchInfo);
        
        if (taskId != null) {
            SaveToBatchTask task = (SaveToBatchTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
        }
        return "/operator/inventory/saveBatchFile.jsp";
    }
    
    @RequestMapping(value="do", params="save")
    public String startTask(HttpServletRequest request, ModelMap model, String taskId, YukonUserContext userContext,
            @ModelAttribute("saveToBatchInfo") SaveToBatchInfo saveToBatchInfo, BindingResult bindingResult) throws ServletRequestBindingException {
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        InventoryCollection inventoryCollection = (InventoryCollection) model.get("inventoryCollection");
                
        // Build map of inventoryId to account Id
        List<Integer> inventoryIds = Lists.newArrayList();
        for (InventoryIdentifier inventory : inventoryCollection) {
            inventoryIds.add(inventory.getInventoryId());
        }
        Map<Integer,Integer> inventoryIdsToAccountIds = customerAccountDao.getAccountIdsByInventoryIds(inventoryIds);

        SaveToBatchTask task = helper.new SaveToBatchTask(userContext, energyCompany, inventoryCollection, 
                inventoryIdsToAccountIds, saveToBatchInfo);
        taskId = helper.startTask(task);
        model.addAttribute("taskId", taskId);
        return "redirect:setup";
    }
    
    @RequestMapping(value="do", params="cancel")
    public String cancel(HttpServletRequest request, YukonUserContext context, ModelMap model) throws ServletRequestBindingException {
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        return "redirect:/spring/stars/operator/inventory/inventoryActions";
    }
    
    @Resource(name="inventoryTaskResultsCache")
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }

}
