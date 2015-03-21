package com.cannontech.web.stars.dr.operator.inventory;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
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

    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private ChangeTypeHelper helper;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private StarsDatabaseCache starsDbCache;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private MemoryCollectionProducer collectionProducer;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private SelectionListService selectionListService;
   
    private RecentResultsCache<AbstractInventoryTask> resultsCache;

    @RequestMapping("view")
    public String view(HttpServletRequest request, ModelMap model, String taskId, LiteYukonUser user) {
        
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        
        String typesListName = YukonSelectionListEnum.DEVICE_TYPE.getListName();
        YukonSelectionList list = selectionListService.getSelectionList(energyCompany, typesListName);
        List<YukonListEntry> deviceTypes = list.getYukonListEntries();
        List<YukonListEntry> validEntries = Lists.newArrayList();
        
        for (YukonListEntry entry : deviceTypes) {
            HardwareType type = HardwareType.valueOf(entry.getDefinition().getDefinitionId());
            if (type.isSupportsChangeType()) {
                validEntries.add(entry);
            }
        }
        
        model.addAttribute("validEntries", validEntries);
        collectionFactory.addCollectionToModelMap(request, model);
        
        if (taskId != null) {
            ChangeTypeTask task = (ChangeTypeTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
        }
        
        return "operator/inventory/changeType.jsp";
    }
    
    @RequestMapping(value="do", params="start")
    public String changeType(HttpServletRequest request, YukonUserContext context, ModelMap model, Integer entry) {
        
        InventoryCollection collection = collectionFactory.createCollection(request);
        YukonListEntry typeEntry = yukonListDao.getYukonListEntry(entry);
        ChangeTypeTask task = helper.new ChangeTypeTask(collection, context, typeEntry);
        String taskId = helper.startTask(task);
        
        model.addAttribute("taskId", taskId);
        collectionFactory.addCollectionToModelMap(request, model);
        
        return "redirect:view";
    }
    
    @RequestMapping("newOperation")
    public String newOperation(ModelMap model, YukonUserContext context, String taskId, String type) {
        
        Status status = Status.valueOf(type);
        ChangeTypeTask task = (ChangeTypeTask) resultsCache.getResult(taskId);
        
        if (status == Status.SUCCESS) {
            Iterator<InventoryIdentifier> successful = task.getSuccessful().iterator();
            addCollectionToModel(model, successful, "successCollectionDescription", context);
        } else {
            Iterator<InventoryIdentifier> unsupported = task.getUnsupported().iterator();
            addCollectionToModel(model, unsupported, "unsupportedCollectionDescription", context);
        }
        
        return "redirect:../inventoryActions";
    }
    
    private void addCollectionToModel(ModelMap model, Iterator<InventoryIdentifier> iterator, 
            String key, YukonUserContext context) {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        String descriptionHint = accessor.getMessage("yukon.web.modules.operator.changeType." + key);
        InventoryCollection collection = collectionProducer.createCollection(iterator, descriptionHint);
        
        model.addAttribute("inventoryCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
    }
    
    @RequestMapping(value="do", params="cancel")
    public String cancel(HttpServletRequest request, ModelMap model) {
        collectionFactory.addCollectionToModelMap(request, model);
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
    @Resource(name="inventoryTaskResultsCache")
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
}