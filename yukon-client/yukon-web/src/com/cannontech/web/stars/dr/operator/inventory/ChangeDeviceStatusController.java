package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeDeviceStatusHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeDeviceStatusHelper.ChangeDeviceStatusTask;

@Controller
@RequestMapping("/operator/inventory/changeStatus/*")
@CheckRoleProperty(YukonRoleProperty.SN_UPDATE_RANGE)
public class ChangeDeviceStatusController {

    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private ChangeDeviceStatusHelper helper;
    @Autowired private YukonEnergyCompanyService energyCompanyService;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    private RecentResultsCache<AbstractInventoryTask> resultsCache;

    @RequestMapping
    public String view(HttpServletRequest request, ModelMap model, String taskId, LiteYukonUser user) throws ServletRequestBindingException {
        YukonEnergyCompany ec = energyCompanyService.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lec = starsDatabaseCache.getEnergyCompany(ec);
        
        YukonSelectionList list = lec.getYukonSelectionList(YukonSelectionListEnum.DEVICE_STATUS.getListName(), true, true);
        List<YukonListEntry> deviceStatusTypes = list.getYukonListEntries();
        
        model.addAttribute("deviceStatusTypes", deviceStatusTypes);
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        
        if (taskId != null) {
            ChangeDeviceStatusTask task = (ChangeDeviceStatusTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
        }
        
        return "operator/inventory/changeStatus.jsp";
    }

    @RequestMapping(value="do", params="start")
    public String startTask(HttpServletRequest request, YukonUserContext context, ModelMap model, int deviceStatusEntryId) throws ServletRequestBindingException {
        InventoryCollection collection = inventoryCollectionFactory.createCollection(request);
        ChangeDeviceStatusTask task = helper.new ChangeDeviceStatusTask(collection, context, deviceStatusEntryId, request.getSession());
        String taskId = helper.startTask(task);
        
        model.addAttribute("taskId", taskId);
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        return "redirect:view";
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