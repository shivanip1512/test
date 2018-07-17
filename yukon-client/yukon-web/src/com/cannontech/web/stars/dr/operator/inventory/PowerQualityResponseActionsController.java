package com.cannontech.web.stars.dr.operator.inventory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.rfn.dao.PqrEventDao;
import com.cannontech.dr.rfn.model.PqrConfig;
import com.cannontech.dr.rfn.model.PqrConfigCommandStatus;
import com.cannontech.dr.rfn.model.PqrConfigResult;
import com.cannontech.dr.rfn.model.PqrEvent;
import com.cannontech.dr.rfn.service.PqrConfigService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@CheckCparm(MasterConfigBoolean.ENABLE_POWER_QUALITY_RESPONSE)
@RequestMapping("/operator/inventory/*")
public class PowerQualityResponseActionsController {
    private static final Logger log = YukonLogManager.getLogger(PowerQualityResponseActionsController.class);
    private static final String reportKeyPrefix = "yukon.web.modules.operator.pqrReport.";
    
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private MemoryCollectionProducer collectionProducer;
    @Autowired private PqrConfigService pqrConfigService;
    @Autowired private PqrConfigValidator pqrConfigValidator;
    @Autowired private PqrEventDao pqrEventDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private enum NewAction implements DisplayableEnum {
        SUCCESSFUL(PqrConfigCommandStatus.SUCCESS),
        UNSUPPORTED(PqrConfigCommandStatus.UNSUPPORTED),
        FAILED(PqrConfigCommandStatus.FAILED),
        ;
        private String keyBase = "yukon.web.modules.operator.pqrConfigResult.newAction.";
        private PqrConfigCommandStatus equivalentConfigStatus;
        
        private NewAction(PqrConfigCommandStatus status) {
            equivalentConfigStatus = status;
        }
        
        @Override
        public String getFormatKey() {
            return keyBase + name();
        }
    }
    
    @RequestMapping(value="pqrReport/setup", method=RequestMethod.GET)
    public String pqrReportSetup(HttpServletRequest req, ModelMap model) {
        inventoryCollectionFactory.addCollectionToModelMap(req, model);
        
        Instant now = Instant.now();
        model.addAttribute("yesterday", now.minus(Duration.standardDays(1)));
        model.addAttribute("now", now);
        
        return "operator/inventory/pqrReport/setup.jsp";
    }
    
    @RequestMapping(value="pqrReport/export", method=RequestMethod.POST)
    public void pqrReportExport(HttpServletResponse resp, YukonUserContext userContext, InventoryCollection collection, 
                                String reportStart, String reportEnd) throws IOException {
        
        log.info("Building PQR event report for " + collection.getCount() + " devices between " + reportStart + " and " 
                 + reportEnd);
        
        // Load events for the given date range and inventoryIds
        DateTimeFormatter eventFormatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE, userContext);
        DateTimeZone timeZone = userContext.getJodaTimeZone();
        Instant startDate = eventFormatter.parseDateTime(reportStart).withTimeAtStartOfDay().withZone(timeZone).toInstant();
        Instant endDate = eventFormatter.parseDateTime(reportEnd).millisOfDay().withMaximumValue().withZone(timeZone).toInstant();
        List<PqrEvent> events = getEvents(startDate, endDate, collection);
        log.debug("Found " + events.size() + " events.");
        
        // Build the Strings for the CSV header row
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = buildHeaderRow(accessor);
        
        // Build the CSV data rows from the events
        List<String[]> dataRows = buildDataRows(events, userContext, accessor);
        
        // Write out the CSV file to the response
        String currentTime = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        String exportFileName = "PqrEventReport_" + currentTime + ".csv";
        WebFileUtils.writeToCSV(resp, headerRow, dataRows, exportFileName);
        log.info("PQR event report complete: " + exportFileName);
    }
    
    @RequestMapping(value="pqrConfig/setup", method={RequestMethod.GET, RequestMethod.POST})
    public String pqrConfigSetup(HttpServletRequest req, ModelMap model, @ModelAttribute("config") PqrConfig config) {
        
        inventoryCollectionFactory.addCollectionToModelMap(req, model);
        model.addAttribute("config", config);
        
        return "operator/inventory/pqrConfig/pqrConfigSetup.jsp";
    }
    
    @RequestMapping(value="pqrConfig/confirm", method=RequestMethod.POST)
    public String pqrConfigConfirm(ModelMap model, InventoryCollection inventoryCollection, 
                                  @ModelAttribute("config") PqrConfig config, BindingResult result, LiteYukonUser user, 
                                  HttpServletRequest req, FlashScope flash) {
        
        if (config.isEmpty()) {
            flash.setError(new YukonMessageSourceResolvable(PqrConfigValidator.keyBase + "emptyConfig"));
            inventoryCollectionFactory.addCollectionToModelMap(req, model);
            model.addAttribute("config", config);
            return "operator/inventory/pqrConfig/pqrConfigSetup.jsp";
        } else {
            pqrConfigValidator.doValidation(config, result);
            if (result.hasErrors()) {
                flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.pqrConfig.validation.invalidConfig"));
                inventoryCollectionFactory.addCollectionToModelMap(req, model);
                model.addAttribute("config", config);
                return "operator/inventory/pqrConfig/pqrConfigSetup.jsp";
            }
        }
        
        inventoryCollectionFactory.addCollectionToModelMap(req, model);
        model.addAttribute("config", config);
        return "operator/inventory/pqrConfig/pqrConfigConfirm.jsp";
    }
    
    @RequestMapping(value="pqrConfig/submit", method=RequestMethod.POST)
    public String pqrConfigSubmit(ModelMap model, InventoryCollection inventoryCollection, 
                                  @ModelAttribute("config") PqrConfig config, BindingResult result, LiteYukonUser user) {
        
        String resultId = pqrConfigService.sendConfigs(inventoryCollection.getList(), config, user);
        
        return "redirect:result/" + resultId;
    }
    
    @RequestMapping(value="pqrConfig/result/{resultId}", method=RequestMethod.GET)
    public String pqrConfigResult(ModelMap model, @PathVariable("resultId") String resultId) {
        
        PqrConfigResult result = pqrConfigService.getResult(resultId)
                                                 .orElseThrow(() -> new IllegalArgumentException("Invalid result id: " 
                                                                                                 + resultId));
        model.addAttribute("resultId", resultId);
        model.addAttribute("result", result);
        
        return "operator/inventory/pqrConfig/pqrConfigResult.jsp";
    }
    
    @RequestMapping(value="pqrConfig/newAction", method=RequestMethod.GET)
    public String newAction(ModelMap model, String resultId, NewAction action, YukonUserContext userContext) {
        PqrConfigResult result = pqrConfigService.getResult(resultId)
                                                 .orElseThrow(() -> new IllegalArgumentException("Invalid result id: " 
                                                                                                 + resultId));
        
        String description = messageSourceResolver.getMessageSourceAccessor(userContext)
                                                  .getMessage(action);
        Iterable<InventoryIdentifier> inventory = getInventoryForNewAction(result, action);
        
        InventoryCollection collection = collectionProducer.createCollection(inventory.iterator(), description);
        model.addAttribute("inventoryCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
    /**
     * Get InventoryIdentifiers of the appropriate inventory from the specified result, for a new action.
     */
    private Iterable<InventoryIdentifier> getInventoryForNewAction(PqrConfigResult result, NewAction action) {
        List<Integer> inventoryIds = result.getInventoryIdsForStatus(action.equivalentConfigStatus);
        return inventoryDao.getYukonInventory(inventoryIds);
    }
    
    /**
     * Retrieve PQR events for the given inventory between the start and end times (inclusive).
     */
    private List<PqrEvent> getEvents(Instant start, Instant end, InventoryCollection collection) {
        Range<Instant> dateRange = Range.inclusive(start, end);
        List<Integer> inventoryIds = collection.getList()
                                               .stream()
                                               .map(InventoryIdentifier::getInventoryId)
                                               .collect(Collectors.toList());
        return pqrEventDao.getEvents(inventoryIds, dateRange);
    }
    
    /**
     * Build a header row (as a String array) from i18n values.
     */
    private String[] buildHeaderRow(MessageSourceAccessor accessor) {
        String serialNumberHeader = accessor.getMessage(reportKeyPrefix + "serialNumber");
        String timestampHeader = accessor.getMessage(reportKeyPrefix + "timestamp");
        String eventTypeHeader = accessor.getMessage(reportKeyPrefix + "eventType");
        String responseTypeHeader = accessor.getMessage(reportKeyPrefix + "responseType");
        String valueHeader = accessor.getMessage(reportKeyPrefix + "value");
        return new String[] {serialNumberHeader, timestampHeader, eventTypeHeader, responseTypeHeader, valueHeader};
    }
    
    /**
     * Build a List of data rows (as a String array) from the PQR events.
     */
    private List<String[]> buildDataRows(List<PqrEvent> events, YukonUserContext userContext, MessageSourceAccessor accessor) {
        List<String[]> dataRows = Lists.newArrayList();
        for(PqrEvent event : events) {
            String[] dataRow = new String[5];
            dataRow[0] = event.getRfnIdentifier().getSensorSerialNumber();
            dataRow[1] = dateFormattingService.format(event.getTimestamp(), DateFormatEnum.BOTH, userContext);
            dataRow[2] = accessor.getMessage(event.getEventType());
            dataRow[3] = accessor.getMessage(event.getResponseType());
            dataRow[4] = event.getValue().toString();
            dataRows.add(dataRow);
        }
        return dataRows;
    }
}
