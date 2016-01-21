package com.cannontech.web.scheduledFileExport.tasks;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.WaterLeakExportGenerationParameters;
import com.cannontech.common.util.Range;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.waterLeakReport.model.SortBy;
import com.cannontech.web.amr.waterLeakReport.model.WaterMeterLeak;
import com.cannontech.web.amr.waterLeakReport.service.WaterMeterLeakService;
import com.google.common.collect.Lists;

public class ScheduledWaterLeakFileExportTask extends ScheduledFileExportTask {
    
    @Autowired private WaterMeterLeakService waterMeterLeakService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceCollectionService deviceCollectionService;
    private static final Logger log = YukonLogManager.getLogger(ScheduledWaterLeakFileExportTask.class);
    
    private int collectionId;
    private int hoursPrevious;
    private int daysOffset;
    private double threshold;
    private boolean includeDisabledPaos;
    
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    
    private String[] getHeaderRow() {
        YukonUserContext userContext = getJob().getUserContext();
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[4];
        headerRow[0] = messageSourceAccessor.getMessage(SortBy.DEVICE_NAME);
        headerRow[1] = messageSourceAccessor.getMessage(SortBy.METER_NUMBER);
        headerRow[2] = messageSourceAccessor.getMessage(SortBy.PAO_TYPE);
        headerRow[3] = messageSourceAccessor.getMessage(SortBy.LEAK_RATE);
        return headerRow;
    }
    
    @Override
    public void start() {
        log.debug("ScheduledWaterLeakFileExportTask started");
        YukonUserContext userContext = getJob().getUserContext();
        
        DateTime now = new DateTime();
        DateTime max = new DateTime(now).minusDays(daysOffset).withTimeAtStartOfDay();
        DateTime min = new DateTime(max).minusHours(hoursPrevious);
        Range<Instant> range = Range.inclusive(new Instant(min), new Instant(max));
        
        if (log.isDebugEnabled()) {
            log.debug("Started at "+df.format(new Date(now.getMillis())));
            log.debug("hoursPrevious="+hoursPrevious);
            log.debug("daysOffset="+daysOffset);
            df.setTimeZone(userContext.getTimeZone());
            log.debug("range " + df.format(new Date(min.getMillis())) + " - " + df.format(new Date(max.getMillis())));
            log.debug("hours between=" + Hours.hoursBetween(range.getMin(), range.getMax()).getHours());
        }
        
        DeviceCollection deviceCollection = deviceCollectionService.loadCollection(collectionId);
        List<SimpleDevice> devices = deviceCollection.getDeviceList();
        log.debug("ScheduledWaterLeakFileExportTask-getLeaks");
        List<WaterMeterLeak> waterLeaks = waterMeterLeakService.getLeaks(devices, range, includeDisabledPaos, threshold, userContext);
        log.debug("Leaks found="+waterLeaks.size());
        log.debug("ScheduledWaterLeakFileExportTask-getLeaks done");
        String[] headerRow = getHeaderRow();
        
        //Get the report data
        List<String[]> dataRows = Lists.newArrayList();
        dataRows.add(headerRow);
        for (WaterMeterLeak waterLeak : waterLeaks) {
            String[] dataRow = new String[5];
            dataRow[0] = waterLeak.getMeter().getName();
            dataRow[1] = waterLeak.getMeter().getMeterNumber();
            dataRow[2] = waterLeak.getMeter().getPaoType().getDbString();
            dataRow[3] = String.valueOf(waterLeak.getLeakRate());
            dataRows.add(dataRow);
        }
        
        //Write the archive file
        File archiveFile = archiveToCsvFile(dataRows);
        
        // Copy the archive file to the export file (if necessary)
        File exportFile = copyExportFile(archiveFile);
        
        // Create a new Export History entry
        ExportHistoryEntry historyEntry = createExportHistoryEntry(FileExportType.WATER_LEAK, archiveFile,
                exportFile, this.getJob().getJobGroupId());

        //Send notification emails
        prepareAndSendNotificationEmails(historyEntry);
    }
    
    @Override
    public void setFileGenerationParameters(ExportFileGenerationParameters parameters) {
        WaterLeakExportGenerationParameters waterLeakParameters = (WaterLeakExportGenerationParameters) parameters;
        hoursPrevious = waterLeakParameters.getHoursPrevious();
        threshold = waterLeakParameters.getThreshold();
        includeDisabledPaos = waterLeakParameters.isIncludeDisabledPaos();
        daysOffset =  waterLeakParameters.getDaysOffset();
        
        DeviceCollection collection = waterLeakParameters.getDeviceCollection();
        collectionId = deviceCollectionService.saveCollection(collection);
    }

    public int getHoursPrevious() {
        return hoursPrevious;
    }

    public void setHoursPrevious(int hoursPrevious) {
        this.hoursPrevious = hoursPrevious;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isIncludeDisabledPaos() {
        return includeDisabledPaos;
    }

    public void setIncludeDisabledPaos(boolean includeDisabledPaos) {
        this.includeDisabledPaos = includeDisabledPaos;
    }
    
    public void setDeviceCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }
    
    public int getDeviceCollectionId() {
        return collectionId;
    }

    public int getDaysOffset() {
        return daysOffset;
    }

    public void setDaysOffset(int daysOffset) {
        this.daysOffset = daysOffset;
    }
    
}