package com.cannontech.web.scheduledFileExport.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.exception.FileCreationException;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.scheduledFileExport.AdeSingleValueFileExportGenerationParameters;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;

/**
 * A task that generates an Archived Data Export file of a specified format
 * on a DeviceCollection.
 */
public class ScheduledAdeFilteredFileExportTask extends ScheduledFileExportTask {
	@Autowired ExportReportGeneratorService exportReportGeneratorService;
	@Autowired MeterDao meterDao;
	@Autowired ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
	
	private DeviceCollection deviceCollection;
	private int exportFormatId;
	
	private Logger log = YukonLogManager.getLogger(ScheduledAdeFilteredFileExportTask.class);
	
	@Override
	public void start() {
		logStart();
		
		DateTime now = new DateTime();
		
		//Set up the File
		File exportFile = getExportFile(now, DEFAULT_FILE_EXTENSION);
		log.debug("Scheduled billing export \"" + name + "\" file name: " + exportFile.getAbsolutePath() + exportFile.getName());
		
		//Generate the data
		List<Meter> meters = meterDao.getMetersForYukonPaos(deviceCollection.getDeviceList());
		ExportFormat format = archiveValuesExportFormatDao.getByFormatId(exportFormatId);
		//TODO: modify to work with Matt's changes
		//List<String> report = exportReportGeneratorService.generateReport(meters, format, getStartOfDay(now), getUserContext());
		
		//Write to the file
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile)))) {
			//for(String row : report) {
			//	writer.write(row);
			//	writer.newLine();
			//}
		} catch(IOException e) {
			throw new FileCreationException("Unable to generate archived data export file due to I/O errors.", e);
		}
		log.debug("Scheduled ADE export \"" + name + "\" file created.");
		
		ExportHistoryEntry historyEntry = addFileToExportHistory(FileExportType.ARCHIVED_DATA_EXPORT, exportFile);
		
		if(StringUtils.isNotEmpty(notificationEmailAddresses)) {
			sendNotificationEmails(historyEntry);
		}
	}

	@Override
	public void setFileGenerationParameters(ExportFileGenerationParameters parameters) {
		AdeSingleValueFileExportGenerationParameters adeParameters = (AdeSingleValueFileExportGenerationParameters) parameters;
		deviceCollection = adeParameters.getDeviceCollection();
		exportFormatId = adeParameters.getFormatId();
	}
	
	private Date getStartOfDay(DateTime now) {
		LocalDate today = now.toLocalDate();
		DateTime startOfToday = today.toDateTimeAtStartOfDay(now.getZone());
		return startOfToday.toDate();
	}
	
	private void logStart() {
		log.info("Starting scheduled file export for schedule \"" + name + "\"");
		String deviceCollectionDescription = deviceCollection.getDescription().getDefaultMessage();
		log.debug("DeviceCollection: " + deviceCollectionDescription);
		String formatName = archiveValuesExportFormatDao.getByFormatId(exportFormatId).getFormatName();
		log.debug("Format: " + formatName);
	}
}
