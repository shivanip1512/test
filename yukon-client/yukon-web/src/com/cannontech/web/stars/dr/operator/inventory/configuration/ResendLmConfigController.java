package com.cannontech.web.stars.dr.operator.inventory.configuration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ResendLmConfigHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ResendLmConfigHelper.ResendLmConfigTask;

@Controller
@RequestMapping("/operator/inventory/resendConfig/*")
@CheckRoleProperty(YukonRoleProperty.SN_UPDATE_RANGE)
public class ResendLmConfigController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private ResendLmConfigHelper helper;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private MemoryCollectionProducer collectionProducer;
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    private final static String key = "yukon.web.modules.operator.inventory.config.send.";
    private final static String views = "operator/inventory/resendConfig/";
    public enum NewOperationType { SUCCESS, FAILED, UNSUPPORTED }
    
    @RequestMapping("setup")
    public String setup(HttpServletRequest req, ModelMap model) {
        collectionFactory.addCollectionToModelMap(req, model);
        return views + "status.jsp";
    }
    
    @RequestMapping("{taskId}/status")
    public String status(HttpServletRequest req, ModelMap model, @PathVariable String taskId) {
        
        ResendLmConfigTask task = (ResendLmConfigTask) resultsCache.getResult(taskId);
        model.addAttribute("task", task);
        model.addAttribute("inventoryCollection", task.getCollection());
        
        return views + "status.jsp";
    }
    
    @RequestMapping(value="do", params="start")
    public String startTask(HttpServletRequest req, YukonUserContext userContext, ModelMap model, boolean inService) {
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        ResendLmConfigTask task = helper.new ResendLmConfigTask(collection, userContext, inService);
        String taskId = helper.startTask(task);
        
        return "redirect:" + taskId + "/status";
    }
    
    @RequestMapping(value="do", params="cancel")
    public String cancel(ModelMap model, HttpServletRequest req, FlashScope flash, String taskId) {
        
        if (StringUtils.isNotBlank(taskId)) {
            AbstractInventoryTask task = resultsCache.getResult(taskId);
            task.cancel();
            int processed = task.getCompletedItems(); 
            flash.setWarning(new YukonMessageSourceResolvable(key + "canceled", processed));
        } else {
            collectionFactory.addCollectionToModelMap(req, model);
            return "redirect:/stars/operator/inventory/inventoryActions";
        }
        
        return "redirect:../home";
    }
    
    @RequestMapping("view-failed")
    public String viewFailed(ModelMap model, String taskId) {
        
        ResendLmConfigTask task = (ResendLmConfigTask) resultsCache.getResult(taskId);
        model.addAttribute("failed", task.getFailures());
        
        return views + "failed.jsp";
    }
    
    @RequestMapping("newOperation")
    public String newOperation(ModelMap model, String taskId, YukonUserContext context, NewOperationType type) {
        
        ResendLmConfigTask task = (ResendLmConfigTask) resultsCache.getResult(taskId);
        String code;
        Iterable<InventoryIdentifier> inventory;
        
        if (type == NewOperationType.SUCCESS) {
            code = key + "successCollectionDescription";
            inventory = task.getSuccessful();
        } else if (type == NewOperationType.FAILED) {
            code = key + "failedCollectionDescription";
            inventory = task.getFailed();
        } else {
            code = key + "unsupportedCollectionDescription";
            inventory = task.getUnsupported();
        }
        
        String description = resolver.getMessageSourceAccessor(context).getMessage(code);
        InventoryCollection temporaryCollection = collectionProducer.createCollection(inventory.iterator(), description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());
        
        return "redirect:../inventoryActions";
    }
    
}