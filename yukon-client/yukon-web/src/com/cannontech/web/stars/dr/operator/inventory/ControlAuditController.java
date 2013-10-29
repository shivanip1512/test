package com.cannontech.web.stars.dr.operator.inventory;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.model.FlotPieDatas;
import com.cannontech.web.common.chart.service.FlotChartService;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditRow;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditSettings;
import com.cannontech.web.stars.dr.operator.inventory.model.ControlAuditResult;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.ControlAuditService;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/operator/inventory/controlAudit/*")
@CheckRoleProperty(YukonRoleProperty.SN_UPDATE_RANGE)
public class ControlAuditController {
    
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    @Autowired private ControlAuditService controlAuditService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private FlotChartService flotChartService;
    
    private RecentResultsCache<ControlAuditResult> resultsCache;
    private static final String BASE_KEY = "yukon.web.modules.operator.controlAudit";
    
    private Validator auditInputValidator = new SimpleValidator<AuditSettings>(AuditSettings.class) {
        @Override
        public void doValidation(AuditSettings auditSettings, Errors errors) {
            Instant now = new Instant();
            Instant from = auditSettings.getFrom();
            Instant to = auditSettings.getTo();
            if (from == null) {
                YukonValidationUtils.rejectValues(errors, BASE_KEY + ".from.error.required", "from");
            } else {
                if (from.isAfter(now)) {
                    YukonValidationUtils.rejectValues(errors, BASE_KEY + ".from.error.future", "from");
                }
            }
            
            if (to == null) {
                YukonValidationUtils.rejectValues(errors, BASE_KEY + ".to.error.required", "to");
            } else {
                if (to.isAfter(now)) {
                    YukonValidationUtils.rejectValues(errors, BASE_KEY + ".to.error.future", "to");
                }
            }

            if (from != null && to != null) {
                if (from.isAfter(to)) {
                    YukonValidationUtils.rejectValues(errors, BASE_KEY + ".range.error.fromAfterTo", "to", "from");
                }
            }
        }
    };
    
    @RequestMapping
    public String view(HttpServletRequest request, ModelMap model, String auditId, YukonUserContext context) throws ServletRequestBindingException {
        
        InventoryCollection collection = inventoryCollectionFactory.addCollectionToModelMap(request, model);
        
        if (auditId == null) {
            AuditSettings settings = new AuditSettings();
            settings.setContext(context);
            settings.setCollection(collection);
            model.addAttribute("settings", settings);
        } else {
            ControlAuditResult audit = resultsCache.getResult(auditId);
            model.addAttribute("audit", audit);
            model.addAttribute("auditId", auditId);
            model.addAttribute("settings", audit.getSettings());
        }
        
        return "operator/inventory/controlAudit/view.jsp";
    }
    
    @RequestMapping
    public String runAudit(@ModelAttribute("settings") AuditSettings settings, BindingResult result,
                           HttpServletRequest request,
                           YukonUserContext context, 
                           ModelMap model, 
                           FlashScope flash) throws ServletRequestBindingException {
        /** gets the collection and also puts it in the model map, which we need if we fail */
        InventoryCollection collection = inventoryCollectionFactory.addCollectionToModelMap(request, model);
        
        auditInputValidator.validate(settings, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            /* TODO create custom binder for this stuff */
            settings.setCollection(collection);
            settings.setContext(context);
            return "operator/inventory/controlAudit/view.jsp";
        }
        
        /* TODO create custom binder for this stuff */
        settings.setCollection(collection);
        settings.setContext(context);
        
        /** Run Audit */
        ControlAuditResult audit = controlAuditService.runAudit(settings);
        audit.setAuditId(resultsCache.addResult(audit));
        
        model.addAttribute("auditId", audit.getAuditId());
        
        inventoryCollectionFactory.addCollectionToModelMap(request, model);
        
        return "redirect:view";
    }
    
    @RequestMapping
    public void download(HttpServletResponse response, YukonUserContext context, String auditId, ResultType type) throws IOException {
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        
        ControlAuditResult result = resultsCache.getResult(auditId);
        List<AuditRow> devices;
        boolean isControlled = false;
        
        if (type == ResultType.CONTROLLED) {
            devices = result.getControlledRows();
            isControlled = true;
        } else if (type == ResultType.UNCONTROLLED) {
            devices = result.getUncontrolledRows();
        } else if (type == ResultType.UNKNOWN) {
            devices = result.getUnknownRows();
        } else {
            devices = result.getUnsupportedRows();
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
            if (isControlled) dataRow[3] = Long.toString(device.getControl().getStandardMinutes());
            
            dataRows.add(dataRow);
        }
        
        //write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "LmControlAudit_" + type + ".csv");
    }
    
    @RequestMapping
    public String newOperation(ModelMap model, String auditId, YukonUserContext context, ResultType type) {
        ControlAuditResult result = resultsCache.getResult(auditId);
        String code = null;
        Iterator<InventoryIdentifier> inventory = null;
        
        switch (type) {
        case CONTROLLED:
            code = "yukon.web.modules.operator.controlAudit.controlledCollectionDescription";
            inventory = result.getControlled().iterator();
            break;
        case UNCONTROLLED:
            code = "yukon.web.modules.operator.controlAudit.uncontrolledCollectionDescription";
            inventory = result.getUncontrolled().iterator();
            break;
        case UNKNOWN:
            code = "yukon.web.modules.operator.controlAudit.unknownCollectionDescription";
            inventory = result.getUnknown().iterator();
            break;
        case UNSUPPORTED:
            code = "yukon.web.modules.operator.controlAudit.unsupportedCollectionDescription";
            inventory = result.getUnsupported().iterator();
            break;
        }
        
        String description = resolver.getMessageSourceAccessor(context).getMessage(code);
        InventoryCollection temporaryCollection = memoryCollectionProducer.createCollection(inventory, description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());
        return "redirect:../inventoryActions";
    }
    
    @Resource(name="controlAuditResultsCache")
    public void setResultsCache(RecentResultsCache<ControlAuditResult> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
    @RequestMapping
    public @ResponseBody JSONObject chart(String auditId, YukonUserContext userContext) {

        MessageSourceAccessor msa = resolver.getMessageSourceAccessor(userContext);
        String controlledStr = msa.getMessage("yukon.web.modules.operator.controlAudit.controlled");
        String uncontrolledStr = msa.getMessage("yukon.web.modules.operator.controlAudit.uncontrolled");
        String unknownStr = msa.getMessage("yukon.web.modules.operator.controlAudit.unknown");
        String unsupportedStr = msa.getMessage("yukon.web.modules.operator.controlAudit.unsupported");
        
        ControlAuditResult result = resultsCache.getResult(auditId);
        
        Map<String, FlotPieDatas> labelDataColorMap = Maps.newLinkedHashMap();
        labelDataColorMap.put(controlledStr, new FlotPieDatas(result.getControlled().getCount(), "#009933")); // .controlled green
        labelDataColorMap.put(uncontrolledStr, new FlotPieDatas(result.getUncontrolled().getCount(), "#fb8521")); // .uncontrolled orange (or ffac00?)
        labelDataColorMap.put(unknownStr, new FlotPieDatas(result.getUnknown().getCount(), "#4d90fe")); // .unknown blue
        labelDataColorMap.put(unsupportedStr, new FlotPieDatas(result.getUnsupported().getCount(), "#888888")); // .unsupported gray

        JSONObject pieJSONData = flotChartService.getPieGraphDataWithColor(labelDataColorMap, true, false, 1.0);
        return pieJSONData;
    }

    @RequestMapping
    public String page(ModelMap model, 
                       ResultType type,
                       String auditId,
                       @RequestParam(defaultValue="10") int itemsPerPage, 
                       @RequestParam(defaultValue="1") int page) {
        
        ControlAuditResult result = resultsCache.getResult(auditId);
        List<AuditRow> inventory = null;
        switch (type) {
        case CONTROLLED:
            inventory = result.getControlledRows();
            break;
        case UNCONTROLLED:
            inventory = result.getUncontrolledRows();
            break;
        case UNKNOWN:
            inventory = result.getUnknownRows();
            break;
        case UNSUPPORTED:
            inventory = result.getUnsupportedRows();
            break;
        }
        
        if (itemsPerPage > CtiUtilities.MAX_ITEMS_PER_PAGE) {
            // Limit the maximum items per page
            itemsPerPage = CtiUtilities.MAX_ITEMS_PER_PAGE;
        }
        SearchResults<AuditRow> pagedRows = SearchResults.pageBasedForWholeList(page, itemsPerPage, inventory);
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
    
    public enum Direction {
        NEXT,
        PREV,
    }
    
    @InitBinder
    public void initBinder(final HttpServletRequest request, WebDataBinder binder, final YukonUserContext context) {
        PropertyEditor propertyEditor = datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM, context, BlankMode.NULL);
        binder.registerCustomEditor(Instant.class, propertyEditor);
    }
    
}