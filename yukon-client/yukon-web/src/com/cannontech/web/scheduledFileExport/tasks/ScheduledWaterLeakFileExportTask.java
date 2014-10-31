package com.cannontech.web.scheduledFileExport.tasks;

import java.io.File;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.WaterLeakExportGenerationParameters;
import com.cannontech.common.util.Range;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.waterLeakReport.model.SortBy;
import com.cannontech.web.amr.waterLeakReport.model.WaterMeterLeak;
import com.cannontech.web.amr.waterLeakReport.service.WaterMeterLeakService;
import com.google.common.collect.Lists;

public class ScheduledWaterLeakFileExportTask extends ScheduledFileExportTask {
    
    @Autowired private WaterMeterLeakService waterMeterLeakService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DeviceCollectionService deviceCollectionService;
    
    private int collectionId;
    private int hoursPrevious;
    private double threshold;
    private boolean includeDisabledPaos;
    
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
        YukonUserContext userContext = getJob().getUserContext();
        
        Duration timePrevious = Duration.standardHours(hoursPrevious);
        Instant now = new Instant();
        Range<Instant> range = Range.inclusive(now, now.minus(timePrevious));
        
        DeviceCollection deviceCollection = deviceCollectionService.loadCollection(collectionId);
        List<SimpleDevice> devices = deviceCollection.getDeviceList();
        List<WaterMeterLeak> waterLeaks = waterMeterLeakService.getLeaks(devices, range, includeDisabledPaos, threshold, userContext);
        String[] headerRow = getHeaderRow();
        
        //Get the report data
        List<String[]> dataRows = Lists.newArrayList();
        dataRows.add(headerRow);
        for (WaterMeterLeak waterLeak : waterLeaks) {
            String[] dataRow = new String[5];
            dataRow[0] = waterLeak.getMeter().getName();
            dataRow[1] = waterLeak.getMeter().getMeterNumber();
            dataRow[2] = waterLeak.getMeter().getPaoType().getDbString();
            dataRow[3] = String.valueOf(waterLeak.getPointValueHolder().getValue());

            String formattedDate = dateFormattingService.format(waterLeak.getPointValueHolder().getPointDataTimeStamp(),
                                                                DateFormatEnum.BOTH, userContext);
            dataRow[4] = formattedDate;
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
    
}