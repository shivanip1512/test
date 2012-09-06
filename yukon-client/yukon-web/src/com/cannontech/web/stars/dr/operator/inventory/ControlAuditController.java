package com.cannontech.web.stars.dr.operator.inventory;

import java.beans.PropertyEditor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
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
    
    private RecentResultsCache<ControlAuditResult> resultsCache;
    
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
    public String runAudit(@ModelAttribute AuditSettings settings, BindingResult result,
                           HttpServletRequest request,
                           YukonUserContext context, 
                           ModelMap model, 
                           FlashScope flash) throws ServletRequestBindingException {
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/inventory/controlAudit/view.jsp";
        }
        
        /* TODO create custom binder for this */
        InventoryCollection collection = inventoryCollectionFactory.createCollection(request);
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
    public String download(HttpServletResponse response, YukonUserContext context, String auditId, ResultType type) throws IOException {
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
            String[] dataRow = new String[4];
            dataRow[0] = device.getHardware().getSerialNumber();
            dataRow[1] = accessor.getMessage(device.getHardware().getInventoryIdentifier().getHardwareType());
            dataRow[2] = device.getHardware().getAccountNo();
            if (isControlled) dataRow[3] = Long.toString(device.getControl().getStandardMinutes());
            
            dataRows.add(dataRow);
        }
        
        //set up output for CSV
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        String fileName = "LmControlAudit_" + type + ".csv";
        fileName = ServletUtil.makeWindowsSafeFileName(fileName);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName);
        OutputStream outputStream = response.getOutputStream();
        
        //write out the file
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
        csvWriter.writeNext(headerRow);
        for (String[] line : dataRows) {
            csvWriter.writeNext(line);
        }
        csvWriter.close();
        return "";
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
    public String chartData(ModelMap model, String auditId) {
        
        ControlAuditResult result = resultsCache.getResult(auditId);
        model.addAttribute("controlled", result.getControlled().getCount());
        model.addAttribute("uncontrolled", result.getUncontrolled().getCount());
        model.addAttribute("unknown", result.getUnknown().getCount());
        model.addAttribute("unsupported", result.getUnsupported().getCount());
        
        return "operator/inventory/controlAudit/piechartDataFile.jsp";
    }
    
    @RequestMapping
    public String chartSettings(ModelMap model) {
        return "operator/inventory/controlAudit/piechartSettings.jsp";
    }
    
    @RequestMapping
    public String page(ModelMap model, ResultType type, int index, Direction direction, String auditId) {
        
        int page = (index / 10) + 1;
        if (direction == Direction.PREV) {
            page--;
        } else {
            page++;
        }
        
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
        
        SearchResult<AuditRow> pagedRows = SearchResult.pageBasedForWholeList(page, 10, inventory);
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