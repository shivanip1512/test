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
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.Range;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.rfn.dao.PqrEventDao;
import com.cannontech.dr.rfn.model.PqrConfig;
import com.cannontech.dr.rfn.model.PqrConfigResult;
import com.cannontech.dr.rfn.model.PqrEvent;
import com.cannontech.dr.rfn.service.PqrConfigService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.util.WebFileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;

@Controller
@CheckCparm(MasterConfigBoolean.ENABLE_POWER_QUALITY_RESPONSE)
public class PowerQualityResponseActionsController {
    private static final Logger log = YukonLogManager.getLogger(PowerQualityResponseActionsController.class);
    private static final String messagePrefix = "yukon.web.modules.operator.pqrReport.";
    
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private PqrConfigService pqrConfigService;
    @Autowired private PqrConfigValidator pqrConfigValidator;
    @Autowired private PqrEventDao pqrEventDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @RequestMapping(value="/operator/inventory/pqrReport/setup", method=RequestMethod.GET)
    public String pqrReportSetup(HttpServletRequest req, ModelMap model) {
        inventoryCollectionFactory.addCollectionToModelMap(req, model);
        
        Instant now = Instant.now();
        model.addAttribute("yesterday", now.minus(Duration.standardDays(1)));
        model.addAttribute("now", now);
        
        return "operator/inventory/pqrReport/setup.jsp";
    }
    
    @RequestMapping(value="/operator/inventory/pqrReport/export", method=RequestMethod.POST)
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
    
    @RequestMapping(value="/operator/inventory/pqrConfig/setup", method=RequestMethod.GET)
    public String pqrConfigSetup(HttpServletRequest req, ModelMap model) {
        
        inventoryCollectionFactory.addCollectionToModelMap(req, model);
        model.addAttribute("config", new PqrConfig());
        
        return "operator/inventory/pqrConfig/pqrConfigSetup.jsp";
    }
    
    @RequestMapping(value="/operator/inventory/pqrConfig/submit", method=RequestMethod.POST)
    public String pqrConfigSubmit(ModelMap model, InventoryCollection inventoryCollection, 
                                  @ModelAttribute("config") PqrConfig config, BindingResult result, LiteYukonUser user, 
                                  HttpServletRequest req, FlashScope flash) {
        
        pqrConfigValidator.doValidation(config, result);
        if (result.hasErrors()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.pqrConfig.validation.invalidConfig"));
            inventoryCollectionFactory.addCollectionToModelMap(req, model);
            model.addAttribute("config", config);
            return "operator/inventory/pqrConfig/pqrConfigSetup.jsp";
        }
        
        //TODO: does this inventory -> hw conversion belong in the config service?
        List<LiteLmHardwareBase> hardware = 
                inventoryBaseDao.getLMHardwareForIds(inventoryCollection.getList()
                                                                        .stream()
                                                                        .map(InventoryIdentifier::getInventoryId)
                                                                        .collect(Collectors.toList()));
        
        String resultId = "";//pqrConfigService.sendConfigs(hardware, config, user);
        
        return "redirect:result/" + resultId;
    }
    
    @RequestMapping(value="/operator/inventory/pqrConfig/result/{resultId}", method=RequestMethod.GET)
    public String pqrConfigResult(ModelMap model, @PathVariable("resultId") String resultId) {
        
        PqrConfigResult result = pqrConfigService.getResult(resultId)
                                                 .orElseThrow(() -> new IllegalArgumentException("Invalid result id: " 
                                                                                                 + resultId));
        model.addAttribute("result", result);
        
        return "operator/inventory/pqrConfig/pqrConfigResult.jsp";
    }
    
    @ResponseBody
    @RequestMapping(value="/operator/inventory/pqrConfig/resultProgress/{resultId}", method=RequestMethod.POST)
    public String pqrResultProgress(@PathVariable("resultId") String resultId) throws JsonProcessingException {
        PqrConfigResult result = pqrConfigService.getResult(resultId)
                                                 .orElseThrow(() -> new IllegalArgumentException("Invalid result id: " 
                                                                                                 + resultId));
        
        return JsonUtils.toJson(result);
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
        String serialNumberHeader = accessor.getMessage(messagePrefix + "serialNumber");
        String timestampHeader = accessor.getMessage(messagePrefix + "timestamp");
        String eventTypeHeader = accessor.getMessage(messagePrefix + "eventType");
        String responseTypeHeader = accessor.getMessage(messagePrefix + "responseType");
        String valueHeader = accessor.getMessage(messagePrefix + "value");
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
