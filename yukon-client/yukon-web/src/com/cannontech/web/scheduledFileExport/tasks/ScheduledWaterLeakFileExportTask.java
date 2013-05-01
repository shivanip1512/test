package com.cannontech.web.scheduledFileExport.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.waterMeterLeak.model.WaterMeterLeak;
import com.cannontech.amr.waterMeterLeak.service.WaterMeterLeakService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.dao.DeviceGroupPermission;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.exception.FileCreationException;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.WaterLeakExportGenerationParameters;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ScheduledWaterLeakFileExportTask extends ScheduledFileExportTask {
	@Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	@Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
	@Autowired private WaterMeterLeakService waterMeterLeakService;
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	@Autowired private DateFormattingService dateFormattingService;
	
	private static final String baseKey = "yukon.web.modules.amr.waterLeakReport.report";
	
	private String uniqueIdentifier = CtiUtilities.getUuidString();
	private Set<SimpleDevice> devices;
    private int hoursPrevious;
    private double threshold;
    private boolean includeDisabledPaos;
    
    private Logger log = YukonLogManager.getLogger(ScheduledWaterLeakFileExportTask.class);
	
    private String[] getHeaderRow() {
    	YukonUserContext userContext = getJobContext().getJob().getUserContext();
    	MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
    	String[] headerRow = new String[4];
        headerRow[0] = messageSourceAccessor.getMessage(baseKey + ".tableHeader.deviceName.linkText");
        headerRow[1] = messageSourceAccessor.getMessage(baseKey + ".tableHeader.meterNumber.linkText");
        headerRow[2] = messageSourceAccessor.getMessage(baseKey + ".tableHeader.deviceType.linkText");
        headerRow[3] = messageSourceAccessor.getMessage(baseKey + ".tableHeader.leakRate.linkText");
        return headerRow;
    }
    
	@Override
	public void start() {
		YukonUserContext userContext = getJobContext().getJob().getUserContext();
		
		Range<Instant> range = new Range<Instant>();
		range.setMin(Instant.now()); //TODO: start of today?
		Duration timePrevious = Duration.standardHours(hoursPrevious);
		range.setMax(Instant.now().minus(timePrevious));
		
		List<WaterMeterLeak> waterLeaks = waterMeterLeakService.getWaterMeterLeaks(devices, range, includeDisabledPaos, threshold, userContext);
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
        
        //Write the file
        File exportFile = getExportFile(DateTime.now(), ".csv");
        try (
        	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile)));
        ){
        	CSVWriter csvWriter = new CSVWriter(writer);
        	csvWriter.writeAll(dataRows);
        } catch(IOException e) {
        	throw new FileCreationException("Unable to generate water leak report file due to I/O errors.", e);
        }
        
        //Add File Export History entry
        ExportHistoryEntry historyEntry = addFileToExportHistory(FileExportType.WATER_LEAK, exportFile);
        
        if(historyEntry == null) {
			log.error("Attempted to send notification for scheduled file export, but export information was not properly archived.");
		} else {
			//send notifications
			if(StringUtils.isNotEmpty(notificationEmailAddresses)) {
				sendNotificationEmails(historyEntry);
			}
		}
	}
	
	@Override
	public void setFileGenerationParameters(ExportFileGenerationParameters parameters) {
		WaterLeakExportGenerationParameters waterLeakParameters = (WaterLeakExportGenerationParameters) parameters;
		devices = Sets.newHashSet(waterLeakParameters.getDeviceCollection().getDeviceList());
		hoursPrevious = waterLeakParameters.getHoursPrevious();
		threshold = waterLeakParameters.getThreshold();
		includeDisabledPaos = waterLeakParameters.isIncludeDisabledPaos();
		
		//Store device list in a device group so that it can persist when the server is shut down.
		//(JobProperty table is not practical for storing a large list of devices)
		StoredDeviceGroup parent = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.AUTO);
		StoredDeviceGroup group = deviceGroupEditorDao.addGroup(parent, DeviceGroupType.STATIC, uniqueIdentifier, DeviceGroupPermission.HIDDEN);
		deviceGroupMemberEditorDao.addDevices(group, devices);
	}
	
	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	//This is the identifier of the device group where the device list was persisted.
	//This is set when the task is recreated on server startup.
	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
		//Pull out devices from group
		StoredDeviceGroup parent = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.AUTO);
		StoredDeviceGroup group = deviceGroupEditorDao.getGroupByName(parent, uniqueIdentifier);
		devices = Sets.newHashSet(deviceGroupMemberEditorDao.getChildDevices(group));
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
	
	public DeviceGroup getDeviceGroup() {
		StoredDeviceGroup parent = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.AUTO);
		return deviceGroupEditorDao.getGroupByName(parent, uniqueIdentifier);
	}
}
