package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeDeviceStatusHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeDeviceStatusHelper.ChangeDeviceStatusTask;

@Controller
@RequestMapping("/operator/inventory/changeStatus/*")
@CheckRoleProperty(YukonRoleProperty.SN_UPDATE_RANGE)
public class ChangeDeviceStatusController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private ChangeDeviceStatusHelper helper;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private StarsDatabaseCache starsDbCache;
    @Autowired private SelectionListService selectionListService;
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    @RequestMapping("setup")
    public String setup(HttpServletRequest req, ModelMap model, LiteYukonUser user) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        
        String statusListName = YukonSelectionListEnum.DEVICE_STATUS.getListName();
        YukonSelectionList statusList = selectionListService.getSelectionList(ec, statusListName);
        List<YukonListEntry> deviceStatusTypes = statusList.getYukonListEntries();
        
        model.addAttribute("deviceStatusTypes", deviceStatusTypes);
        collectionFactory.addCollectionToModelMap(req, model);
        
        return "operator/inventory/changeStatus.jsp";
    }
    
    @RequestMapping("{taskId}/status")
    public String view(ModelMap model, @PathVariable String taskId, LiteYukonUser user) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        
        String statusListName = YukonSelectionListEnum.DEVICE_STATUS.getListName();
        YukonSelectionList statusList = selectionListService.getSelectionList(ec, statusListName);
        List<YukonListEntry> deviceStatusTypes = statusList.getYukonListEntries();
        
        model.addAttribute("deviceStatusTypes", deviceStatusTypes);
        
        ChangeDeviceStatusTask task = (ChangeDeviceStatusTask) resultsCache.getResult(taskId);
        model.addAttribute("task", task);
        model.addAttribute("inventoryCollection", task.getCollection());
        
        return "operator/inventory/changeStatus.jsp";
    }
    
    @RequestMapping(value="do", params="start")
    public String startTask(HttpServletRequest req, YukonUserContext userContext, int deviceStatusEntryId) {
        
        HttpSession session = req.getSession();
        InventoryCollection collection = collectionFactory.createCollection(req);
        ChangeDeviceStatusTask task = 
                helper.new ChangeDeviceStatusTask(collection, userContext, deviceStatusEntryId, session);
        String taskId = helper.startTask(task);
        
        return "redirect:" + taskId + "/status";
    }
    
    @RequestMapping(value="do", params="cancel")
    public String cancel(HttpServletRequest request, YukonUserContext context, ModelMap model) {
        collectionFactory.addCollectionToModelMap(request, model);
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
}