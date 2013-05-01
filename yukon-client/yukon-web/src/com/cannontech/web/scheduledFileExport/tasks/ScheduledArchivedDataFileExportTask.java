package com.cannontech.web.scheduledFileExport.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.dataRange.ChangeIdRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRangeType;
import com.cannontech.amr.archivedValueExporter.model.dataRange.LocalDateRange;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
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
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.scheduledFileExport.ArchivedDataExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.dao.ScheduledFileExportDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.RawPointHistoryDao;

public class ScheduledArchivedDataFileExportTask extends ScheduledFileExportTask {
	@Autowired private ExportReportGeneratorService exportReportGeneratorService;
	@Autowired private MeterDao meterDao;
	@Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
	@Autowired private RawPointHistoryDao rawPointHistoryDao;
	@Autowired private ScheduledFileExportDao scheduledFileExportDao;
	@Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	@Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
	
	private String uniqueIdentifier = CtiUtilities.getUuidString();
	private List<SimpleDevice> deviceList;
	private int formatId;
	private Set<Attribute> attributes;
	private DataRange dataRange = new DataRange();
	
	private Logger log = YukonLogManager.getLogger(ScheduledArchivedDataFileExportTask.class);
	
	@Override
	public void start() {
		List<Meter> meters = meterDao.getMetersForYukonPaos(deviceList);
		ExportFormat format = archiveValuesExportFormatDao.getByFormatId(formatId);
		populateDataRange();
		
		//Get the report data
		Attribute[] attributesArray = attributes.toArray(new Attribute[attributes.size()]);
		List<String> report = exportReportGeneratorService.generateReport(meters, format, dataRange, getUserContext(), attributesArray);
		
		//If using SINCE_LAST_CHANGE_ID, update last id for this job
		if(dataRange.getDataRangeType() == DataRangeType.SINCE_LAST_CHANGE_ID) {
			scheduledFileExportDao.setRphIdForJob(this.getJobContext().getJob().getId(), dataRange.getChangeIdRange().getLastChangeId());
		}
		
		//Write the file
		File exportFile = getExportFile(DateTime.now() ,".csv");
		try (
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile)));
		){
			for(String row : report) {
				writer.write(row);
				writer.newLine();
			}
		} catch(IOException e) {
			throw new FileCreationException("Unable to generate billing file due to I/O errors.", e);
		}
		
		//Add File Export History entry
		ExportHistoryEntry historyEntry = addFileToExportHistory(FileExportType.ARCHIVED_DATA_EXPORT, exportFile);
		
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
		ArchivedDataExportFileGenerationParameters adeParameters = (ArchivedDataExportFileGenerationParameters) parameters;
		deviceList = adeParameters.getDeviceCollection().getDeviceList();
		formatId = adeParameters.getFormatId();
		attributes = adeParameters.getAttributes();
		dataRange = adeParameters.getDataRange();
		
		//Store device list in a device group so that it can persist when the server is shut down.
		//(JobProperty table is not practical for storing a large list of devices)
		StoredDeviceGroup parent = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.AUTO);
		StoredDeviceGroup group = deviceGroupEditorDao.addGroup(parent, DeviceGroupType.STATIC, uniqueIdentifier, DeviceGroupPermission.HIDDEN);
		deviceGroupMemberEditorDao.addDevices(group, deviceList);
	}
	
	private void populateDataRange() {
		if(dataRange.getDataRangeType() == DataRangeType.END_DATE) {
			dataRange.setEndDate(LocalDate.now());
		} else if(dataRange.getDataRangeType() == DataRangeType.SINCE_LAST_CHANGE_ID) {
			long firstChangeId = scheduledFileExportDao.getLastRphIdByJobId(this.getJobContext().getJob().getId());
			long lastChangeId = rawPointHistoryDao.getMaxChangeId();
			ChangeIdRange changeIdRange = new ChangeIdRange();
			changeIdRange.setFirstChangeId(firstChangeId);
			changeIdRange.setLastChangeId(lastChangeId);
			dataRange.setChangeIdRange(changeIdRange);
		}
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
		deviceList = deviceGroupMemberEditorDao.getChildDevices(group);
	}

	public int getFormatId() {
		return formatId;
	}

	public void setFormatId(int formatId) {
		this.formatId = formatId;
	}

	public Set<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public String getDataRangeType() {
		return dataRange.getDataRangeType().name();
	}
	
	public void setDataRangeType(String dataRangeType) {
		dataRange.setDataRangeType(DataRangeType.valueOf(dataRangeType));
	}
	
	public int getDataRangeDaysPrevious() {
		return dataRange.getDaysPrevious();
	}
	
	public void setDataRangeDaysPrevious(int daysPrevious) {
		dataRange.setDaysPrevious(daysPrevious);
	}
	
	public long getDataRangeSinceLastChangeIdFirst() {
		if(dataRange.getChangeIdRange() == null) {
			dataRange.setChangeIdRange(new ChangeIdRange());
		}
		return dataRange.getChangeIdRange().getFirstChangeId();
	}
	
	public void setDataRangeSinceLastChangeIdFirst(long firstChangeId) {
		if(dataRange.getChangeIdRange() == null) {
			dataRange.setChangeIdRange(new ChangeIdRange());
		}
		dataRange.getChangeIdRange().setFirstChangeId(firstChangeId);
	}
	
	public long getDataRangeSinceLastChangeIdLast() {
		if(dataRange.getChangeIdRange() == null) {
			dataRange.setChangeIdRange(new ChangeIdRange());
		}
		return dataRange.getChangeIdRange().getLastChangeId();
	}
	
	public void setDataRangeSinceLastChangeIdLast(long lastChangeId) {
		if(dataRange.getChangeIdRange() == null) {
			dataRange.setChangeIdRange(new ChangeIdRange());
		}
		dataRange.getChangeIdRange().setLastChangeId(lastChangeId);
	}
	
	public Date getDataRangeDateRangeStart() {
		if(dataRange.getLocalDateRange() == null) {
			dataRange.setLocalDateRange(new LocalDateRange());
		}
		return dataRange.getLocalDateRange().getStartDate().toDate();
	}
	
	public void setDataRangeDateRangeStart(Date startDate) {
		if(dataRange.getLocalDateRange() == null) {
			dataRange.setLocalDateRange(new LocalDateRange());
		}
		dataRange.getLocalDateRange().setStartDate(new LocalDate(startDate));
	}
	
	public Date getDataRangeDateRangeEnd() {
		if(dataRange.getLocalDateRange() == null) {
			dataRange.setLocalDateRange(new LocalDateRange());
		}
		return dataRange.getLocalDateRange().getEndDate().toDate();
	}
	
	public void setDataRangeDateRangeEnd(Date endDate) {
		if(dataRange.getLocalDateRange() == null) {
			dataRange.setLocalDateRange(new LocalDateRange());
		}
		dataRange.getLocalDateRange().setEndDate(new LocalDate(endDate));
	}
	
	public DataRange getDataRange() {
		return dataRange;
	}
	
	public DeviceGroup getDeviceGroup() {
	    StoredDeviceGroup parent = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.AUTO);
		return deviceGroupEditorDao.getGroupByName(parent, uniqueIdentifier);
	}
}
