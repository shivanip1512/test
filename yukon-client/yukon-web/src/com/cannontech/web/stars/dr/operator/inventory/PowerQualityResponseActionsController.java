package com.cannontech.web.stars.dr.operator.inventory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.dr.rfn.dao.PqrEventDao;
import com.cannontech.dr.rfn.model.PqrEvent;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@CheckCparm(MasterConfigBoolean.ENABLE_POWER_QUALITY_RESPONSE)
public class PowerQualityResponseActionsController {
    private static final Logger log = YukonLogManager.getLogger(PowerQualityResponseActionsController.class);
    private static final String messagePrefix = "yukon.web.modules.operator.pqrReport.";
    
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private PqrEventDao pqrEventDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @RequestMapping("/operator/inventory/pqrReport/setup")
    public String pqrReportSetup(HttpServletRequest req, ModelMap model) {
        inventoryCollectionFactory.addCollectionToModelMap(req, model);
        
        Instant now = Instant.now();
        model.addAttribute("yesterday", now.minus(Duration.standardDays(1)));
        model.addAttribute("now", now);
        
        return "operator/inventory/pqrReport/setup.jsp";
    }
    
    @RequestMapping(value="/operator/inventory/pqrReport/export")
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
