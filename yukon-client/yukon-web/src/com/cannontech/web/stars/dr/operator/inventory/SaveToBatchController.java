package com.cannontech.web.stars.dr.operator.inventory;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.xml.serialize.AddressingGroup;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.SaveToBatchHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeDeviceStatusHelper.ChangeDeviceStatusTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.SaveToBatchHelper.SaveToBatchTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/operator/inventory/saveToBatch/*")
                 
public class SaveToBatchController {
    
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private SaveToBatchHelper helper;
    @Autowired private LoadGroupDao loadGroupDao;
    //@Autowired private StaticLoadGroupMappingDao staticLoadGroupMappingDao;   May be necessary if Xcel uses save to batch functionality.
    private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    public final static String useCurrentRoutes = "useCurrentRoutes";
    public final static String useCurrentGroups = "useCurrentGroups";
    public final static String useEcDefaultRoute = "useEcDefaultRoute";
    public final static String yukonDefaultRoute = "0";
    
    @RequestMapping
    public String setup(HttpServletRequest request, ModelMap model, String taskId, YukonUserContext userContext) throws ServletRequestBindingException, RemoteException {
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        LiteStarsEnergyCompany liteStarsEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompany);
        
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        InventoryCollection inventoryCollection = (InventoryCollection) model.get("inventoryCollection");
        SaveToBatchInfo saveToBatchInfo = new SaveToBatchInfo();
        saveToBatchInfo.setRouteId(useCurrentRoutes);
        saveToBatchInfo.setGroupId(useCurrentGroups);
        model.addAttribute("saveToBatchInfo", saveToBatchInfo);
        
        Boolean isUniformHardwareConfigType = true;
        InventoryIdentifier firstInventoryIdentifier = inventoryCollection.iterator().next();
        HardwareConfigType firstHardwareConfigType = firstInventoryIdentifier.getHardwareType().getHardwareConfigType();
        for (InventoryIdentifier inventory : inventoryCollection) {
            HardwareConfigType nextHardwareConfigType = inventory.getHardwareType().getHardwareConfigType();
            if (nextHardwareConfigType != firstHardwareConfigType) {
                isUniformHardwareConfigType = false;
            }
        }
        model.addAttribute("uniformHardwareConfigType", isUniformHardwareConfigType);
        LiteYukonPAObject defaultRoute = liteStarsEnergyCompany.getDefaultRoute();
        String ecDefaultRoute = null;
        if (defaultRoute == null) {
            ecDefaultRoute = "none";
        } else {
            ecDefaultRoute = ((Integer)defaultRoute.getPaoIdentifier().getPaoId()).toString();
        }
        model.addAttribute("ecDefaultRoute", ecDefaultRoute);
        List<LiteYukonPAObject> routes = liteStarsEnergyCompany.getAllRoutes();
        model.addAttribute("routes", routes);
        
        // Get all load groups that are in all assigned programs
        Set<LoadGroup> groups = Sets.newHashSet();
        StarsApplianceCategory[] starsApplianceCategories = liteStarsEnergyCompany.getStarsEnrollmentPrograms().getStarsApplianceCategory();
        for (StarsApplianceCategory appCat : starsApplianceCategories) {
            StarsEnrLMProgram[] starsPrograms = appCat.getStarsEnrLMProgram();
            for (StarsEnrLMProgram starsProgram : starsPrograms) {
                AddressingGroup[] addressingGroups = starsProgram.getAddressingGroup();
                for (AddressingGroup addressingGroup : addressingGroups) {
                    LoadGroup loadGroup = loadGroupDao.getById(addressingGroup.getEntryID());
                    if(loadGroup.getPaoIdentifier().getPaoId() > 0) {
                        groups.add(loadGroup);
                    }
                }
            }
        }
        model.addAttribute("groups", groups);
        model.addAttribute("useCurrentGroups", useCurrentGroups);
        model.addAttribute("useCurrentRoutes", useCurrentRoutes);
        model.addAttribute("useEcDefaultRoute", useEcDefaultRoute);
        model.addAttribute("yukonDefaultRoute", yukonDefaultRoute);
        if (taskId != null) {
            SaveToBatchTask task = (SaveToBatchTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
        }
        return "/operator/inventory/saveBatch.jsp";
    }
    
    @RequestMapping(value="do", params="start")
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

        String routeId = saveToBatchInfo.getRouteId();
        String groupId = saveToBatchInfo.getGroupId();
        
        SaveToBatchTask task = helper.new SaveToBatchTask(userContext, energyCompany, inventoryCollection, 
                inventoryIdsToAccountIds, routeId, groupId);
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
