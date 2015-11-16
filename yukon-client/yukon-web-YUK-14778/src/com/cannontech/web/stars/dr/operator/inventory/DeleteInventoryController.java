package com.cannontech.web.stars.dr.operator.inventory;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.DeleteInventoryHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.DeleteInventoryHelper.DeleteInventoryTask;

@Controller
@RequestMapping("/operator/inventory/deleteInventory/*")
@CheckRoleProperty(YukonRoleProperty.SN_DELETE_RANGE)
public class DeleteInventoryController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private DeleteInventoryHelper helper;
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    @RequestMapping("setup")
    public String setup(HttpServletRequest req, ModelMap model) {
        collectionFactory.addCollectionToModelMap(req, model);
        return "operator/inventory/deleteInventory.jsp";
    }
    
    @RequestMapping("{taskId}/status")
    public String view(ModelMap model, @PathVariable String taskId) {
        
        DeleteInventoryTask task = (DeleteInventoryTask) resultsCache.getResult(taskId);
        model.addAttribute("task", task);
        model.addAttribute("inventoryCollection", task.getCollection());
        
        return "operator/inventory/deleteInventory.jsp";
    }
    
    @RequestMapping(value="delete", params="start")
    public String delete(HttpServletRequest req, YukonUserContext userContext, ModelMap model) {
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        DeleteInventoryTask task = helper.new DeleteInventoryTask(collection, userContext);
        String taskId = helper.startTask(task);
        
        model.addAttribute("taskId", taskId);
        collectionFactory.addCollectionToModelMap(req, model);
        
        return "redirect:" + taskId + "/status";
    }
    
    @RequestMapping(value="delete", params="cancel")
    public String cancel(HttpServletRequest req, ModelMap model) {
        collectionFactory.addCollectionToModelMap(req, model);
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
}