package com.cannontech.web.scheduledFileExport.tasks;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.scheduledFileExport.AdeMultiValueFileExportGenerationParameters;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.dao.ScheduledFileExportDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.google.common.collect.Lists;

/**
 * A task that generates an Archived Data Export file of a specified format
 * on a DeviceCollection.
 */
public class ScheduledAdeAllFileExportTask extends ScheduledFileExportTask {
	@Autowired ExportReportGeneratorService exportReportGeneratorService;
	@Autowired MeterDao meterDao;
	@Autowired ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
	@Autowired ScheduledFileExportDao scheduledFileExportDao;
	@Autowired RawPointHistoryDao rawPointHistoryDao;
	
	private DeviceCollection deviceCollection;
	private int exportFormatId;
	private List<Attribute> attributes;
	private boolean isLastRphId;
	private int daysPrevious;
	private long lastRphId;
	private long currentMaxRphId;
	
	private Logger log = YukonLogManager.getLogger(ScheduledAdeAllFileExportTask.class);
	
	@Override
	public void start() {
		if(isLastRphId) {
			int jobId = getJobContext().getJob().getId();
			lastRphId = scheduledFileExportDao.getLastRphIdByJobId(jobId);
			currentMaxRphId = rawPointHistoryDao.getMaxChangeId();
		}
		
		logStart();
		
		DateTime now = new DateTime();
		
		//Set up the File
		File exportFile = getExportFile(now, DEFAULT_FILE_EXTENSION);
		log.debug("Scheduled billing export \"" + name + "\" file name: " + exportFile.getAbsolutePath() + exportFile.getName());
		
		//TODO: interface with Matt's code
		//Generate the data
		//List<Meter> meters = meterDao.getMetersForYukonPaos(deviceCollection.getDeviceList());
		//ExportFormat format = archiveValuesExportFormatDao.getByFormatId(exportFormatId);
		//List<String> report = exportReportGeneratorService.mattsMethod()
		
		//Write to the file
		/*
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile)))) {
			for(String row : report) {
				writer.write(row);
				writer.newLine();
			}
		} catch(IOException e) {
			throw new FileCreationException("Unable to generate archived data export file due to I/O errors.", e);
		}
		log.debug("Scheduled ADE export \"" + name + "\" file created.");
		*/
		
		ExportHistoryEntry historyEntry = addFileToExportHistory(FileExportType.ARCHIVED_DATA_EXPORT, exportFile);
		
		if(StringUtils.isNotEmpty(notificationEmailAddresses)) {
			sendNotificationEmails(historyEntry);
		}
	}

	@Override
	public void setFileGenerationParameters(ExportFileGenerationParameters parameters) {
		AdeMultiValueFileExportGenerationParameters adeParameters = (AdeMultiValueFileExportGenerationParameters) parameters;
		deviceCollection = adeParameters.getDeviceCollection();
		exportFormatId = adeParameters.getFormatId();
		attributes = adeParameters.getAttributes();
		isLastRphId = adeParameters.isLastRphId();
		if(!isLastRphId) {
			daysPrevious = adeParameters.getDaysPrevious();
		}
	}
	
	private void logStart() {
		log.info("Starting scheduled file export for schedule \"" + name + "\"");
		String deviceCollectionDescription = deviceCollection.getDescription().getDefaultMessage();
		log.debug("DeviceCollection: " + deviceCollectionDescription);
		String formatName = archiveValuesExportFormatDao.getByFormatId(exportFormatId).getFormatName();
		log.debug("Format: " + formatName);
		List<String> attributeStrings = Lists.newArrayList();
		for(Attribute attribute : attributes) {
			attributeStrings.add(attribute.getMessage().getDefaultMessage());
		}
		log.debug(attributeStrings.toString());
		if(isLastRphId) {
			log.debug("Last RPH ID: " + lastRphId);
			log.debug("Current max RPH ID : " + currentMaxRphId);
		} else {
			log.debug("Days Previous: " + daysPrevious);
		}
	}
}
