package com.cannontech.web.stars.dr.operator.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.HardwareConfigService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.hardware.service.impl.HardwareShedRestoreLoadServiceImpl;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.model.ControlAuditTask;
import com.cannontech.web.stars.dr.operator.inventory.model.ShedRestoreLoad;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeDeviceStatusHelper.ChangeDeviceStatusTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeServiceCompanyHelper.ChangeServiceCompanyTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeTypeHelper.ChangeTypeTask;
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
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private HardwareConfigService hardwareConfigService;
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    @Autowired private HardwareShedRestoreLoadServiceImpl hardwareService;
    
    private static final int maxInventory = 1000;
    private static final String idListKey = "yukon.common.device.bulk.bulkAction.collection.idList";
    private static final String view = "operator/inventory/";
    
    /** Inventory Action Status Redirect */
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
        } else if (task instanceof ControlAuditTask) {
            return "redirect:/stars/operator/inventory/controlAudit/" + id + "/status";
        } else if (task instanceof ChangeTypeTask) {
            return "redirect:/stars/operator/inventory/changeType/" + id + "/status";
        }
        
        resp.setStatus(HttpStatus.NOT_FOUND.value());
        return null;
    }
    
    /** Recent inventory actions. */
    @RequestMapping("actions/recent")
    public @ResponseBody List<Map<String, Object>> recent(YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        EnergyCompany energyCompany = ecDao.getEnergyCompany(userContext.getYukonUser());
        List<Integer> validEcIds = Lists.transform(energyCompany.getDescendants(true), EnergyCompanyDao.TO_ID_FUNCTION);
        
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        List<AbstractInventoryTask> recentTasks = new ArrayList<>(resultsCache.getTasks().values());
        Collections.sort(recentTasks);
        
        for (AbstractInventoryTask task : recentTasks) {
            EnergyCompany ec = ecDao.getEnergyCompany(task.getUserContext().getYukonUser());
            // to view the recent asset action the user needs to be from the same ec as the user who created the
            // action or to be from a parent company
            if (validEcIds.contains(ec.getId())) {
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
        }
        
        return tasks;
    }
    
    /** Inventory Actions */
    @RequestMapping(value="inventoryActions", method = RequestMethod.GET)
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
    
    /** Inventory Configuration */
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

    @RequestMapping("disable")
    public String disable(ModelMap model, int inventoryId, YukonUserContext userContext, FlashScope flashScope) {

        // Log hardware disable attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.hardwareDisableAttempted(userContext.getYukonUser(),
            lmHardwareBase.getManufacturerSerialNumber(), CtiUtilities.STRING_NONE, EventSource.OPERATOR);

        model.addAttribute("inventoryId", inventoryId);

        try {
            hardwareConfigService.disable(inventoryId, CtiUtilities.NONE_ZERO_ID, -1, userContext);

            MessageSourceResolvable confirmationMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.disableCommandSent");
            flashScope.setConfirm(confirmationMessage);
        } catch (CommandCompletionException e) {
            MessageSourceResolvable errorMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.disableCommandFailed",
                    e.getMessage());
            flashScope.setError(errorMessage);
        }

        return "redirect:/stars/operator/inventory/view";
    }

    @RequestMapping("enable")
    public String enable(ModelMap model, int inventoryId, YukonUserContext userContext, FlashScope flashScope) {

        // Log hardware enable attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.hardwareEnableAttempted(userContext.getYukonUser(),
            lmHardwareBase.getManufacturerSerialNumber(), CtiUtilities.STRING_NONE, EventSource.OPERATOR);

        model.addAttribute("inventoryId", inventoryId);

        try {
            hardwareConfigService.enable(inventoryId, CtiUtilities.NONE_ZERO_ID, -1, userContext);

            MessageSourceResolvable confirmationMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.enableCommandSent");
            flashScope.setConfirm(confirmationMessage);
        } catch (CommandCompletionException e) {
            MessageSourceResolvable errorMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.enableCommandFailed",
                    e.getMessage());
            flashScope.setError(errorMessage);
        }

        return "redirect:/stars/operator/inventory/view";
    }

    
    /** Inventory Collection Popup Table */
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
    
    @RequestMapping(value = "/shedRestoreLoad", method = RequestMethod.POST)
    @ResponseBody
    public void shedRestoreLoad(@ModelAttribute("shedRestoreLoad") ShedRestoreLoad shedRestoreLoad,
            YukonUserContext userContext, FlashScope flash) {

        Map<String, Object> resultMap = new HashMap<>();
        int duration = shedRestoreLoad.getDuration();
        if (duration != 0) {
            resultMap = hardwareService.shedLoad(shedRestoreLoad.getInventoryId(), shedRestoreLoad.getRelayNo(), duration, userContext);
        } else {
            resultMap = hardwareService.restoreLoad(shedRestoreLoad.getInventoryId(), shedRestoreLoad.getRelayNo(), userContext);
        }

        MessageSourceResolvable responseMsg = YukonMessageSourceResolvable.createDefaultWithoutCode((String) resultMap.get("message"));
        if ((boolean) resultMap.get("success")) {
            flash.setMessage(responseMsg, FlashScopeMessageType.SUCCESS);
        } else {
            flash.setMessage(responseMsg, FlashScopeMessageType.ERROR);
        }
    }

    @RequestMapping("/shedRestoreLoadPopup/{inventoryId}")
    public String shedRestoreLoadPopup(@PathVariable int inventoryId, ModelMap model,  YukonUserContext userContext) {

        boolean isAllowDRControl =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_DR_CONTROL, userContext.getYukonUser());
        if (isAllowDRControl) {
            model.addAttribute("duration", TimeIntervals.getShedRestoreTimeOptions());
        } else {
            model.addAttribute("interval", TimeIntervals.MINUTES_5);
        }

        List<Integer> relayNo = new ArrayList<>(Stream.of(1, 2, 3, 4, 5).collect(Collectors.toList()));

        model.addAttribute("relayNo", relayNo);
        ShedRestoreLoad shedRestoreLoad = new ShedRestoreLoad(inventoryId);
        model.addAttribute("shedRestoreLoad", shedRestoreLoad);
        model.addAttribute("isAllowDRControl", isAllowDRControl);
        
        return "operator/inventory/shedRestoreLoad.jsp";
    }

}