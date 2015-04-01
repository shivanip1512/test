package com.cannontech.web.stars.dr.operator.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeDeviceStatusHelper.ChangeDeviceStatusTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeServiceCompanyHelper.ChangeServiceCompanyTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeWarehouseHelper.ChangeWarehouseTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.DeleteInventoryHelper.DeleteInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.NewLmConfigHelper.NewLmConfigTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ResendLmConfigHelper.ResendLmConfigTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.SaveToBatchHelper.SaveToBatchTask;
import com.google.common.collect.Lists;

@RequestMapping("/operator/inventory/*")
@Controller
@CheckRole(YukonRole.INVENTORY)
public class InventoryActionsController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private MemoryCollectionProducer collectionProducer;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private ConfigurationSource configSource;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private DateFormattingService dateFormatting;
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    private static final int maxInventory = 1000;
    private static final String idListKey = "yukon.common.device.bulk.bulkAction.collection.idList";
    private static final String view = "operator/inventory/";
    
    /* Inventory Action Status Redirect */
    @RequestMapping("action/{id}")
    public String action(HttpServletResponse resp, @PathVariable String id) {
        
        AbstractInventoryTask task = resultsCache.getResult(id);
        
        if (task instanceof ResendLmConfigTask) {
            return "redirect:/stars/operator/inventory/resendConfig/" + id + "/status";
        } else if (task instanceof NewLmConfigTask) {
            return "redirect:/stars/operator/inventory/actions/config/" + id + "/status";
        } else if (task instanceof ChangeServiceCompanyTask) {
            return "redirect:/stars/operator/inventory/changeServiceCompany/" + id + "/status";
        } else if (task instanceof DeleteInventoryTask) {
            return "redirect:/stars/operator/inventory/deleteInventory/" + id + "/status";
        } else if (task instanceof ChangeDeviceStatusTask) {
            return "redirect:/stars/operator/inventory/changeStatus/" + id + "/status";
        } else if (task instanceof ChangeWarehouseTask) {
            return "redirect:/stars/operator/inventory/changeWarehouse/" + id + "/status";
        } else if (task instanceof SaveToBatchTask) {
            return "redirect:/stars/operator/inventory/saveToBatch/" + id + "/status";
        }
        // TODO Control audits
        
        resp.setStatus(HttpStatus.NOT_FOUND.value());
        return null;
    }
    
    /* Inventory Action Status Redirect */
    @RequestMapping("actions/recent")
    public @ResponseBody List<Map<String, Object>> recent(YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        List<AbstractInventoryTask> recentTasks = new ArrayList<>(resultsCache.getTasks().values());
        Collections.sort(recentTasks);
        
        for (AbstractInventoryTask task : recentTasks) {
            Map<String, Object> data = new HashMap<>();
            Map<String, Object> text = new HashMap<>();
            
            long startTime = task.getStartedAt();
            data.put("id", task.getTaskId());
            data.put("startedAt", startTime);
            data.put("completed", task.getCompletedItems());
            data.put("total", task.getTotalItems());
            data.put("complete", task.isComplete());
            
            String formatted = dateFormatting.format(startTime, DateFormatEnum.DATEHM, userContext);
            text.put("startedAt", formatted);
            text.put("type", accessor.getMessage(task));
            
            data.put("text", text);
            tasks.add(data);
        }
        
        return tasks;
    }
    
    /* Inventory Actions */
    @RequestMapping("inventoryActions")
    public String inventoryActions(HttpServletRequest req, ModelMap model, YukonUserContext userContext) {
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        String displayHint = accessor.getMessage(idListKey);
        
        InventoryCollection memoryCollection = collectionProducer.createCollection(collection.iterator(), displayHint);
        
        model.addAttribute("inventoryCollection", memoryCollection);
        model.addAllAttributes(memoryCollection.getCollectionParameters());
        
        boolean digiEnabled = configSource.getBoolean(MasterConfigBoolean.DIGI_ENABLED);
        model.addAttribute("digiEnabled", digiEnabled);
        
        boolean showSaveToFile = configSource.getBoolean(MasterConfigBoolean.ENABLE_INVENTORY_SAVE_TO_FILE);
        model.addAttribute("showSaveToFile", showSaveToFile);
        
        return view + "actions.jsp";
    }
    
    /* Inventory Configuration */
    @RequestMapping("inventoryConfiguration")
    public String inventoryConfiguration(HttpServletRequest req, ModelMap model, YukonUserContext userContext) {
        
        LiteYukonUser user = userContext.getYukonUser();
        rolePropertyDao.checkProperty(YukonRoleProperty.DEVICE_RECONFIG, user);
        
        boolean showNewConfig =  configSource.getBoolean(MasterConfigBoolean.SEND_INDIVIDUAL_SWITCH_CONFIG, false);
        model.addAttribute("showNewConfig", showNewConfig);
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        String displayHint = accessor.getMessage(idListKey);
        
        InventoryCollection memoryCollection = collectionProducer.createCollection(collection.iterator(), displayHint);
        
        model.addAttribute("inventoryCollection", memoryCollection);
        model.addAllAttributes(memoryCollection.getCollectionParameters());
        
        return view + "inventoryConfiguration.jsp";
    }
    
    /* Inventory Collection Popup Table */
    @RequestMapping("selectedInventoryTable")
    public String selectedInventoryTable(HttpServletRequest req, ModelMap model) {
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        int totalInventoryCount = collection.getCount();
        List<InventoryIdentifier> inventoryToLoad = collection.getSubList(0, maxInventory);
        
        List<DisplayableLmHardware> displayableLmHardware = inventoryDao.getDisplayableLMHardware(inventoryToLoad);
        
        List<List<String>> inventoryInfoList = Lists.newArrayList();
        for (DisplayableLmHardware inventory : displayableLmHardware) {
            List<String> row = Lists.newArrayList();
            row.add(inventory.getSerialNumber());
            row.add(inventory.getDeviceType());
            row.add(inventory.getLabel());
            inventoryInfoList.add(row);
        }
        
        if (totalInventoryCount > maxInventory) {
            model.addAttribute("resultsLimitedTo", maxInventory);
        }
        model.addAttribute("inventoryInfoList", inventoryInfoList);
        
        return view + "selectedInventoryPopup.jsp";
    }
    
}