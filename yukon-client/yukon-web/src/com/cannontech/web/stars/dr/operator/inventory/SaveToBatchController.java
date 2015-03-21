package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.SaveToBatchHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.SaveToBatchHelper.SaveToBatchTask;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/operator/inventory/saveToBatch/*")
public class SaveToBatchController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private StarsDatabaseCache starsDbCache;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private SaveToBatchHelper helper;
    @Autowired private PaoDao paoDao;
    @Autowired private DefaultRouteService defaultRouteService;

    private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    @RequestMapping("setup")
    public String setup(HttpServletRequest req, ModelMap model, String taskId, YukonUserContext userContext) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        InventoryCollection collection = collectionFactory.addCollectionToModelMap(req, model);
        
        Boolean isUniformHardwareConfigType = true;
        InventoryIdentifier firstIdentifier = collection.iterator().next();
        HardwareConfigType firstConfigType = firstIdentifier.getHardwareType().getHardwareConfigType();
        for (InventoryIdentifier inventory : collection) {
            HardwareConfigType nextHardwareConfigType = inventory.getHardwareType().getHardwareConfigType();
            if (nextHardwareConfigType != firstConfigType) {
                isUniformHardwareConfigType = false;
                break;
            }
        }
        model.addAttribute("uniformHardwareConfigType", isUniformHardwareConfigType);
        
        String ecDefaultRoute;
        // All routeId's must be positive.  If this value persists
        // beyond the try block below, then there is no default route.
        int ecDefaultRouteId = -1;
        try {
            ecDefaultRoute = paoDao.getYukonPAOName(defaultRouteService.getDefaultRouteId(ec));
            ecDefaultRoute = accessor.getMessage("yukon.common.route.default") + ecDefaultRoute;
        } catch(NotFoundException e) {
            ecDefaultRoute = accessor.getMessage("yukon.common.route.default.none");
        }
        List<LiteYukonPAObject> routes = ecDao.getAllRoutes(ec);
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
    public String startTask(HttpServletRequest req, ModelMap model, String taskId, YukonUserContext userContext,
            @ModelAttribute("saveToBatchInfo") SaveToBatchInfo saveToBatchInfo, BindingResult result) {
        
        YukonEnergyCompany ec = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        InventoryCollection collection = collectionFactory.addCollectionToModelMap(req, model);
                
        // Build map of inventoryId to account Id
        List<Integer> inventoryIds = Lists.newArrayList();
        for (InventoryIdentifier inventory : collection) {
            inventoryIds.add(inventory.getInventoryId());
        }
        Map<Integer, Integer> inventoryIdsToAccountIds = customerAccountDao.getAccountIdsByInventoryIds(inventoryIds);
        
        SaveToBatchTask task = helper.new SaveToBatchTask(userContext, ec, collection, inventoryIdsToAccountIds, 
                saveToBatchInfo);
        taskId = helper.startTask(task);
        model.addAttribute("taskId", taskId);
        
        return "redirect:setup";
    }
    
    @RequestMapping(value="do", params="cancel")
    public String cancel(HttpServletRequest req, ModelMap model) {
        collectionFactory.addCollectionToModelMap(req, model);
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
    @Resource(name="inventoryTaskResultsCache")
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
}