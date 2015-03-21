package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
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
    private RecentResultsCache<AbstractInventoryTask> resultsCache;

    @RequestMapping("view")
    public String view(HttpServletRequest request, ModelMap model, String taskId, LiteYukonUser user) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        
        String statusListName = YukonSelectionListEnum.DEVICE_STATUS.getListName();
        YukonSelectionList statusList = selectionListService.getSelectionList(ec, statusListName);
        List<YukonListEntry> deviceStatusTypes = statusList.getYukonListEntries();
        
        model.addAttribute("deviceStatusTypes", deviceStatusTypes);
        collectionFactory.addCollectionToModelMap(request, model);
        
        if (taskId != null) {
            ChangeDeviceStatusTask task = (ChangeDeviceStatusTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
        }
        
        return "operator/inventory/changeStatus.jsp";
    }

    @RequestMapping(value="do", params="start")
    public String startTask(HttpServletRequest req, YukonUserContext context, ModelMap model, int deviceStatusEntryId) {
        
        HttpSession session = req.getSession();
        InventoryCollection collection = collectionFactory.createCollection(req);
        ChangeDeviceStatusTask task = helper.new ChangeDeviceStatusTask(collection, context, deviceStatusEntryId, session);
        String taskId = helper.startTask(task);
        
        model.addAttribute("taskId", taskId);
        collectionFactory.addCollectionToModelMap(req, model);
        
        return "redirect:view";
    }
    
    @RequestMapping(value="do", params="cancel")
    public String cancel(HttpServletRequest request, YukonUserContext context, ModelMap model) {
        collectionFactory.addCollectionToModelMap(request, model);
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
    @Resource(name="inventoryTaskResultsCache")
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
}