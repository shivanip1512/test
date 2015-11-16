package com.cannontech.web.scheduledFileExport.tasks;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.paoPointValue.model.MeterPointValue;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.MeterEventsExportGenerationParameters;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.service.PaoPointValueService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class ScheduledMeterEventsFileExportTask extends ScheduledFileExportTask {
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	@Autowired private PaoPointValueService paoPointValueService;
	@Autowired private AttributeService attributeService;
	@Autowired private PointFormattingService pointFormattingService;
	@Autowired private DeviceCollectionService deviceCollectionService;
	
	private final static Set<String> NORMAL_VALUES = ImmutableSet.of(OutageStatus.GOOD.name().toLowerCase(),
            EventStatus.CLEARED.name().toLowerCase());
	
	private int daysPrevious;
	private boolean onlyLatestEvent;
	private boolean onlyAbnormalEvents;
	private boolean includeDisabledDevices;
	private int collectionId;
	private Set<? extends Attribute> attributes;
	
	@Override
	public void start() {
		List<String[]> dataRows = getDataRows();
        
        //Write the archive file
		File archiveFile = archiveToCsvFile(dataRows);
		
		// Copy the archive file to the export file (if necessary)
		File exportFile = copyExportFile(archiveFile);
  		
  		// Create a new Export History entry
        ExportHistoryEntry historyEntry = createExportHistoryEntry(FileExportType.METER_EVENTS, archiveFile, exportFile,
                this.getJob().getJobGroupId());
	  		  
  		//Send notification emails
        prepareAndSendNotificationEmails(historyEntry);
	}
	
	@Override
	public void setFileGenerationParameters(ExportFileGenerationParameters parameters) {
		MeterEventsExportGenerationParameters meterEventsParameters = (MeterEventsExportGenerationParameters) parameters;
		daysPrevious = meterEventsParameters.getDaysPrevious();
		onlyAbnormalEvents = meterEventsParameters.isOnlyAbnormalEvents();
		onlyLatestEvent = meterEventsParameters.isOnlyLatestEvent();
		includeDisabledDevices = meterEventsParameters.isIncludeDisabledDevices();
		attributes = meterEventsParameters.getAttributes();
		
		DeviceCollection collection = meterEventsParameters.getDeviceCollection();
		collectionId = deviceCollectionService.saveCollection(collection);
	}

	public int getDaysPrevious() {
		return daysPrevious;
	}

	public void setDaysPrevious(int daysPrevious) {
		this.daysPrevious = daysPrevious;
	}

	public boolean isOnlyLatestEvent() {
		return onlyLatestEvent;
	}

	public void setOnlyLatestEvent(boolean onlyLatestEvent) {
		this.onlyLatestEvent = onlyLatestEvent;
	}

	public boolean isOnlyAbnormalEvents() {
		return onlyAbnormalEvents;
	}

	public void setOnlyAbnormalEvents(boolean onlyAbnormalEvents) {
		this.onlyAbnormalEvents = onlyAbnormalEvents;
	}

	public boolean isIncludeDisabledDevices() {
		return includeDisabledDevices;
	}

	public void setIncludeDisabledDevices(boolean includeDisabledDevices) {
		this.includeDisabledDevices = includeDisabledDevices;
	}

	public Set<? extends Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<? extends Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public int getDeviceCollectionId() {
	    return collectionId;
	}
	
	public void setDeviceCollectionId(int collectionId) {
	    this.collectionId = collectionId;
	}
	
	/*
	 * Runs the meter events report using the parameters stored in this object. Returns a List of rows,
	 * where each row is a String[], to be written out to csv. 
	 */
	private List<String[]> getDataRows() {
		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(getUserContext());
        
        DateTimeZone userTimeZone = getUserContext().getJodaTimeZone();
        LocalDate localDate = new LocalDate(userTimeZone);
        Instant fromInstant = TimeUtil.toMidnightAtBeginningOfDay(localDate, userTimeZone).minus(Duration.standardDays(daysPrevious));
        Instant toInstant = TimeUtil.toMidnightAtEndOfDay(localDate, userTimeZone);
        
        DeviceCollection collection = deviceCollectionService.loadCollection(collectionId);
        List<SimpleDevice> devices = collection.getDeviceList();
        
        List<MeterPointValue> events = paoPointValueService.getMeterPointValues(devices, attributes,
                Range.inclusiveExclusive(fromInstant, toInstant),
                onlyLatestEvent ? 1 : null,
                includeDisabledDevices,
                onlyAbnormalEvents ? NORMAL_VALUES : null,
                getUserContext());
        
        List<String[]> dataRows = Lists.newArrayList();
        
        String[] headerRow = new String[5];
        headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.deviceName.linkText");
        headerRow[1] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.meterNumber.linkText");
        headerRow[2] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.date.linkText");
        headerRow[3] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.event.linkText");
        headerRow[4] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.value.linkText");
        dataRows.add(headerRow);
        
        for(MeterPointValue event : events) {
            String[] dataRow = new String[5];
            dataRow[0] = event.getMeter().getName();
            dataRow[1] = event.getMeter().getMeterNumber();
            
            DateTime timeStamp = new DateTime(event.getPointValueHolder().getPointDataTimeStamp(), userTimeZone);
            String dateTimeString = timeStamp.toString(DateTimeFormat.mediumDateTime());
            dataRow[2] = dateTimeString;
            
            dataRow[3] = event.getPointName();
            
            String valueString = pointFormattingService.getValueString(event.getPointValueHolder(), Format.VALUE, getUserContext());
            dataRow[4] = valueString;
            dataRows.add(dataRow);
        }
        
        return dataRows;
	}
}
