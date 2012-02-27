package com.cannontech.web.stars.dr.operator.inventory;

import java.util.Iterator;
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
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask.Status;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeTypeHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeTypeHelper.ChangeTypeTask;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/operator/inventory/changeType/*")
@CheckRoleProperty(YukonRoleProperty.SN_UPDATE_RANGE)
public class ChangeDeviceTypeController {

    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private ChangeTypeHelper helper;
    @Autowired private YukonEnergyCompanyService energyCompanyService;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    private RecentResultsCache<AbstractInventoryTask> resultsCache;

    @RequestMapping
    public String view(HttpServletRequest request, ModelMap model, String taskId, LiteYukonUser user) throws ServletRequestBindingException {
        YukonEnergyCompany ec = energyCompanyService.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lec = starsDatabaseCache.getEnergyCompany(ec);
        
        YukonSelectionList list = lec.getYukonSelectionList(YukonSelectionListEnum.DEVICE_TYPE.getListName());
        List<YukonListEntry> deviceTypes = list.getYukonListEntries();
        List<Pair<HardwareType, String>> validTypes = Lists.newArrayList();
        
        for (YukonListEntry entry : deviceTypes) {
            HardwareType type = HardwareType.valueOf(entry.getDefinition().getDefinitionId());
            if (type.isSupportsChangeType()) {
                validTypes.add(new Pair<HardwareType, String>(type, entry.getEntryText()));
            }
        }
        
        model.addAttribute("validTypes", validTypes);
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        
        if (taskId != null) {
            ChangeTypeTask task = (ChangeTypeTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
        }
        
        return "operator/inventory/changeType.jsp";
    }

    @RequestMapping(value="do", params="start")
    public String changeType(HttpServletRequest request, YukonUserContext context, ModelMap model, HardwareType type) throws ServletRequestBindingException {
        InventoryCollection collection = inventoryCollectionFactory.createCollection(request);
        ChangeTypeTask task = helper.new ChangeTypeTask(collection, context, type);
        String taskId = helper.startTask(task);
        
        model.addAttribute("taskId", taskId);
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        return "redirect:view";
    }
    
    @RequestMapping
    public String newOperation(ModelMap model, YukonUserContext context, String taskId, String type) {
        Status status = Status.valueOf(type);
        ChangeTypeTask task = (ChangeTypeTask) resultsCache.getResult(taskId);
        if (status == Status.SUCCESS) {
            addCollectionToModel(model, taskId, task.getSuccessful().iterator(), "successCollectionDescription", context);
        } else {
            addCollectionToModel(model, taskId, task.getUnsupported().iterator(), "unsupportedCollectionDescription", context);
        }
        return "redirect:../inventoryActions";
    }
    
    private void addCollectionToModel(ModelMap model, String taskId, Iterator<InventoryIdentifier> iterator, String key, YukonUserContext context) {
        String descriptionHint = resolver.getMessageSourceAccessor(context).getMessage("yukon.web.modules.operator.changeType." + key);
        InventoryCollection temporaryCollection = memoryCollectionProducer.createCollection(iterator, descriptionHint);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());
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