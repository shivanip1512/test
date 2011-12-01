package com.cannontech.web.stars.dr.operator.inventory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.DeleteInventoryHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.DeleteInventoryHelper.DeleteInventoryTask;

@Controller
@RequestMapping("/operator/inventory/deleteInventory/*")
@CheckRoleProperty(YukonRoleProperty.SN_DELETE_RANGE)
public class DeleteInventoryController {
    
    private @Autowired InventoryCollectionFactoryImpl inventoryCollectionFactory;
    private @Autowired DeleteInventoryHelper helper;
    private RecentResultsCache<AbstractInventoryTask> resultsCache;

    @RequestMapping
    public String view(HttpServletRequest request, ModelMap model, String taskId) throws ServletRequestBindingException {
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        
        if (taskId != null) {
            DeleteInventoryTask task = (DeleteInventoryTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
        }
        
        return "operator/inventory/deleteInventory.jsp";
    }

    @RequestMapping(value="delete", params="start")
    public String delete(HttpServletRequest request, YukonUserContext context, ModelMap model) throws ServletRequestBindingException {
        InventoryCollection collection = inventoryCollectionFactory.createCollection(request);
        DeleteInventoryTask task = helper.new DeleteInventoryTask(collection, context);
        String taskId = helper.startTask(task);
        
        model.addAttribute("taskId", taskId);
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        return "redirect:view";
    }
    
    @RequestMapping(value="delete", params="cancel")
    public String cancel(HttpServletRequest request, YukonUserContext context, ModelMap model) throws ServletRequestBindingException {
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        return "redirect:/spring/stars/operator/inventory/inventoryActions";
    }
    
    @Resource(name="inventoryTaskResultsCache")
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
}