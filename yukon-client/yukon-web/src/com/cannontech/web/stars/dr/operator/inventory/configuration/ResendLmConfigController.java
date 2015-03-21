package com.cannontech.web.stars.dr.operator.inventory.configuration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
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
    
    private RecentResultsCache<AbstractInventoryTask> resultsCache;
    private final static String key = "yukon.web.modules.operator.inventory.config.send.";
    public enum NewOperationType { SUCCESS, FAILED, UNSUPPORTED }
    
    @RequestMapping("view")
    public String view(HttpServletRequest request, ModelMap model, String taskId, LiteYukonUser user) 
    throws ServletRequestBindingException {
        
        collectionFactory.addCollectionToModelMap(request, model);
        
        if (taskId != null) {
            ResendLmConfigTask task = (ResendLmConfigTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
        }
        
        return "operator/inventory/resendConfig/status.jsp";
    }
    
    @RequestMapping(value="do", params="start")
    public String startTask(HttpServletRequest req, YukonUserContext context, ModelMap model, boolean inService) {
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        ResendLmConfigTask task = helper.new ResendLmConfigTask(collection, context, inService);
        String taskId = helper.startTask(task);
        
        model.addAttribute("taskId", taskId);
        collectionFactory.addCollectionToModelMap(req, model);
        
        return "redirect:view";
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
        
        return "operator/inventory/resendConfig/failed.jsp";
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
    
    @Resource(name="inventoryTaskResultsCache")
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
}