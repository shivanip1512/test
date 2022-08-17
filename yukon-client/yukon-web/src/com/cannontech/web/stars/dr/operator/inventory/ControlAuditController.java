package com.cannontech.web.stars.dr.operator.inventory;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditRow;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditSettings;
import com.cannontech.web.stars.dr.operator.inventory.model.ControlAuditTask;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.ControlAuditService;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/operator/inventory/controlAudit/*")
@CheckRoleProperty(YukonRoleProperty.SN_UPDATE_RANGE)
public class ControlAuditController {
    
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    @Autowired private ControlAuditService controlAuditService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DateFormattingService dateFormattingService;
    
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    private static final String key = "yukon.web.modules.operator.controlAudit.";
    private static final String errorKey = "yukon.web.error";
    
    private Validator validator = new SimpleValidator<AuditSettings>(AuditSettings.class) {
        @Override
        public void doValidation(AuditSettings auditSettings, Errors errors) {
            Instant now = new Instant();
            Instant from = auditSettings.getFrom();
            Instant to = auditSettings.getTo();
            if (from == null) {
                YukonValidationUtils.rejectValues(errors, errorKey + ".date.validRequired", "from");
            } else {
                if (from.isAfter(now)) {
                    YukonValidationUtils.rejectValues(errors, errorKey + ".date.inThePast", "from");
                }
            }
            
            if (to == null) {
                YukonValidationUtils.rejectValues(errors, errorKey + ".date.validRequired", "to");
            } else {
                if (to.isAfter(now)) {
                    YukonValidationUtils.rejectValues(errors, errorKey + ".date.inThePast", "to");
                }
            }
            
            if (from != null && to != null) {
                if (from.isAfter(to)) {
                    YukonValidationUtils.rejectValues(errors, errorKey + ".date.fromAfterTo", "to", "from");
                }
            }
        }
    };
    
    @RequestMapping("setup")
    public String setup(HttpServletRequest req, ModelMap model, YukonUserContext userContext) {
        
        inventoryCollectionFactory.addCollectionToModelMap(req, model);
        
        AuditSettings settings = new AuditSettings();
        model.addAttribute("settings", settings);
        
        return "operator/inventory/controlAudit/view.jsp";
    }
    
    @RequestMapping("{taskId}/status")
    public String status(ModelMap model, @PathVariable String taskId) {
        
        ControlAuditTask audit = (ControlAuditTask) resultsCache.getResult(taskId);
        model.addAttribute("audit", audit);
        model.addAttribute("settings", audit.getSettings());
        model.addAttribute("inventoryCollection", audit.getCollection());
        
        return "operator/inventory/controlAudit/view.jsp";
    }
    
    @RequestMapping("start")
    public String start(@ModelAttribute("settings") AuditSettings settings, BindingResult result,
                           YukonUserContext userContext,
                           InventoryCollection collection,
                           ModelMap model, 
                           FlashScope flash) {
        
        validator.validate(settings, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            
            model.addAttribute("inventoryCollection", collection);
            model.addAllAttributes(collection.getCollectionParameters());
            
            return "operator/inventory/controlAudit/view.jsp";
        }
        
        /** Run Audit */
        String taskId = controlAuditService.start(settings, collection, userContext);
        
        return "redirect:" + taskId + "/status";
    }
    
    @RequestMapping("download")
    public void download(HttpServletResponse resp, YukonUserContext userContext, String auditId, ResultType type) 
    throws IOException {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        
        ControlAuditTask audit = (ControlAuditTask) resultsCache.getResult(auditId);
        List<AuditRow> devices;
        boolean isControlled = false;
        
        if (type == ResultType.CONTROLLED) {
            devices = audit.getControlled();
            isControlled = true;
        } else if (type == ResultType.UNCONTROLLED) {
            devices = audit.getUncontrolled();
        } else if (type == ResultType.UNKNOWN) {
            devices = audit.getUnknown();
        } else {
            devices = audit.getUnsupported();
        }
        
        String[] headerRow = new String[3];
        if (isControlled) headerRow = new String[4];
        
        headerRow[0] = "SERIAL_NUMBER";
        headerRow[1] = "DEVICE_TYPE";
        headerRow[2] = "ACCOUNT_NUMBER";
        if (isControlled) headerRow[3] = "CONTROL_TOTAL_MINUTES";
        
        List<String[]> dataRows = Lists.newArrayList();
        for(AuditRow device: devices) {
            String[] dataRow = new String[3];
            if (isControlled) dataRow = new String[4];
            dataRow[0] = device.getHardware().getSerialNumber();
            dataRow[1] = accessor.getMessage(device.getHardware().getInventoryIdentifier().getHardwareType());
            dataRow[2] = device.getHardware().getAccountNo();
            if (isControlled) {
                if (device.getControl() != null) {
                    dataRow[3] = Long.toString(device.getControl().getStandardMinutes());
                } else {
                    dataRow[3] = accessor.getMessage("yukon.common.na");
                }
            }
            
            dataRows.add(dataRow);
        }
        
        //write out the file
        
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(resp, headerRow, dataRows, "LmControlAudit_" + type + "_" + now + ".csv");
    }
    
    @RequestMapping("new-action")
    public String newAction(ModelMap model, YukonUserContext userContext, String auditId, ResultType type) {
        
        ControlAuditTask audit = (ControlAuditTask) resultsCache.getResult(auditId);
        String code = null;
        Iterator<InventoryIdentifier> inventory = null;
        
        switch (type) {
        case CONTROLLED:
            code = key + "controlledCollectionDescription";
            inventory = Lists.transform(audit.getControlled(), YukonInventory.TO_IDENTIFIER).iterator();
            break;
            
        case UNCONTROLLED:
            code = key + "uncontrolledCollectionDescription";
            inventory = Lists.transform(audit.getUncontrolled(), YukonInventory.TO_IDENTIFIER).iterator();
            break;
            
        case UNKNOWN:
            code = key + "unknownCollectionDescription";
            inventory = Lists.transform(audit.getUnknown(), YukonInventory.TO_IDENTIFIER).iterator();
            break;
            
        case UNSUPPORTED:
            code = key + "unsupportedCollectionDescription";
            inventory = Lists.transform(audit.getUnsupported(), YukonInventory.TO_IDENTIFIER).iterator();
            break;
        }
        
        String description = resolver.getMessageSourceAccessor(userContext).getMessage(code);
        InventoryCollection temporaryCollection = memoryCollectionProducer.createCollection(inventory, description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());
        
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
    @RequestMapping("{taskId}/details")
    public String details(ModelMap model, @PathVariable String taskId) {
        
        ControlAuditTask audit = (ControlAuditTask) resultsCache.getResult(taskId);
        model.addAttribute("audit", audit);
        model.addAttribute("inventoryCollection", audit.getCollection());
        
        return "operator/inventory/controlAudit/result.details.jsp";
    }
    
    @RequestMapping("{taskId}/update")
    public @ResponseBody Map<String, Object> update(@PathVariable String taskId, YukonUserContext userContext) {
        
        Map<String, Object> status = new HashMap<>();
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        
        ControlAuditTask audit = (ControlAuditTask) resultsCache.getResult(taskId);
        
        double completed = audit.getCompletedItems();
        
        DecimalFormat df = new DecimalFormat("##0.###");
        df.setMaximumFractionDigits(3);
        
        /** Build data for progress bar. */
        status.put("complete", audit.isComplete());
        status.put("completed", completed);
        status.put("total", audit.getTotalItems());
        
        /** Build data for pie chart. */
        List<Map<String, Object>> data = new ArrayList<>();
        
        Map<String, Object> controlled = new HashMap<>();
        controlled.put("name", accessor.getMessage(key + "controlled"));
        double controlledPercent = 0.0;
        if (completed > 0) {
            controlledPercent = audit.getControlled().size() / completed * 100;
        }
        controlled.put("y", Double.parseDouble(df.format(controlledPercent)));
        controlled.put("color", "#009933");
        data.add(controlled);
        
        Map<String, Object> uncontrolled = new HashMap<>();
        uncontrolled.put("name", accessor.getMessage(key + "uncontrolled"));
        double uncontrolledPercent = 0.0;
        if (completed > 0) {
            uncontrolledPercent = audit.getUncontrolled().size() / completed * 100;
        }
        uncontrolled.put("y", Double.parseDouble(df.format(uncontrolledPercent)));
        uncontrolled.put("color", "#fb8521");
        data.add(uncontrolled);
        
        Map<String, Object> unknown = new HashMap<>();
        unknown.put("name", accessor.getMessage(key + "unknown"));
        double unknownPercent = 0.0;
        if (completed > 0) {
            unknownPercent = audit.getUnknown().size() / completed * 100;
        }
        unknown.put("y", Double.parseDouble(df.format(unknownPercent)));
        unknown.put("color", "#4d90fe");
        data.add(unknown);
        
        Map<String, Object> unsupported = new HashMap<>();
        unsupported.put("name", accessor.getMessage(key + "unsupported"));
        double unsupportedPercent = 0.0;
        if (completed > 0) {
            unsupportedPercent = audit.getUnsupported().size() / completed * 100;
        }
        unsupported.put("y", Double.parseDouble(df.format(unsupportedPercent)));
        unsupported.put("color", "#888888");
        data.add(unsupported);
        
        status.put("data", data);
        
        return status;
    }
    
    @RequestMapping("page")
    public String page(ModelMap model, 
                       ResultType type,
                       String auditId,
                       @DefaultItemsPerPage(10) PagingParameters paging) {
        
        ControlAuditTask audit = (ControlAuditTask) resultsCache.getResult(auditId);
        List<AuditRow> inventory = null;
        switch (type) {
        case CONTROLLED:
            inventory = audit.getControlled();
            break;
        case UNCONTROLLED:
            inventory = audit.getUncontrolled();
            break;
        case UNKNOWN:
            inventory = audit.getUnknown();
            break;
        case UNSUPPORTED:
            inventory = audit.getUnsupported();
            break;
        }
        
        SearchResults<AuditRow> pagedRows = SearchResults.pageBasedForWholeList(paging, inventory);
        model.addAttribute("result", pagedRows);
        model.addAttribute("type", type);
        model.addAttribute("auditId", auditId);
        
        return "operator/inventory/controlAudit/table.jsp";
    }
    
    public enum ResultType {
        CONTROLLED,
        UNCONTROLLED,
        UNKNOWN,
        UNSUPPORTED
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext context) {
        PropertyEditor propertyEditor = 
                datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM, context, BlankMode.NULL);
        binder.registerCustomEditor(Instant.class, propertyEditor);
    }
    
}