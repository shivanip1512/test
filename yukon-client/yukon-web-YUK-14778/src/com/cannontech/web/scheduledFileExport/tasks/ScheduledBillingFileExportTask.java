package com.cannontech.web.scheduledFileExport.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.billing.mainprograms.BillingBean;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.exception.FileCreationException;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.scheduledFileExport.BillingFileExportGenerationParameters;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;
import com.google.common.collect.Lists;

/**
 * A task that generates a billing file of a specified format on a set of DeviceGroups.
 */
public class ScheduledBillingFileExportTask extends ScheduledFileExportTask implements PersistedFormatTask {
	private List<String> deviceGroupNames = Lists.newArrayList();
	private int fileFormatId;
	private int demandDays;
	private int energyDays;
	private boolean removeMultiplier;
	
	private Logger log = YukonLogManager.getLogger(ScheduledBillingFileExportTask.class);
	
	@Override
	public void start() {
		logStart();
		
		DateTime now = new DateTime();
		BillingBean billingBean = getPopulatedBillingBean(now);
		
		//Set up the File
		String fileExtension = FileFormatTypes.getFileExtensionByFormatId(fileFormatId);
		File archiveFile = createArchiveFile(now, fileExtension);
        log.debug("Scheduled billing export \"" + name + "\" file name: " + archiveFile.getAbsolutePath()
            + archiveFile.getName());
		
		//Write out the billing file
		try (OutputStream outputStream = new FileOutputStream(archiveFile)){
			billingBean.generateFile(outputStream);
		} catch(IOException e) {
			throw new FileCreationException("Unable to generate billing file due to I/O errors.", e);			
		}
		log.debug("Scheduled billing export \"" + name + "\" file created.");
		
		// Copy the archive file to the export file (if necessary)
		File exportFile = copyExportFile(archiveFile);
		
		// Create new File Export History entry
        ExportHistoryEntry historyEntry = createExportHistoryEntry(FileExportType.BILLING,
                                              archiveFile, exportFile, this.getJob().getJobGroupId());
		
		//Send notification emails
        prepareAndSendNotificationEmails(historyEntry);
	}
	
	@Override
	public void setFileGenerationParameters(ExportFileGenerationParameters parameters) {
		BillingFileExportGenerationParameters billingParameters = (BillingFileExportGenerationParameters) parameters;
		for(DeviceGroup group : billingParameters.getDeviceGroups()) {
			deviceGroupNames.add(group.getFullName());
		}
		fileFormatId = billingParameters.getFormatId();
		demandDays = billingParameters.getDemandDaysPrevious();
		energyDays = billingParameters.getEnergyDaysPrevious();
		removeMultiplier = billingParameters.isRemoveMultiplier();
	}
	
	public void setDeviceGroupNames(List<String> deviceGroupNames) {
		this.deviceGroupNames = deviceGroupNames;
	}
	
	public List<String> getDeviceGroupNames() {
		return deviceGroupNames;
	}
	
	public void setFileFormatId(int fileFormatId) {
		this.fileFormatId = fileFormatId;
	}
	
	public int getFileFormatId() {
		return fileFormatId;
	}
	
	public void setDemandDays(int demandDays) {
		this.demandDays = demandDays;
	}
	
	public int getDemandDays() {
		return demandDays;
	}
	
	public void setEnergyDays(int energyDays) {
		this.energyDays = energyDays;
	}
	
	public int getEnergyDays() {
		return energyDays;
	}
	
	public void setRemoveMultiplier(boolean removeMultiplier) {
		this.removeMultiplier = removeMultiplier;
	}
	
	public boolean isRemoveMultiplier() {
		return removeMultiplier;
	}
	
	@Override
	public int getFormatId() {
	    return fileFormatId;
	}
	
	//Creates a BillingBean configured with the appropriate parameters for this export task.
	private BillingBean getPopulatedBillingBean(DateTime now) {
		BillingBean billingBean = new BillingBean();
		billingBean.setFileFormat(fileFormatId);
		billingBean.setAppendToFile(false);
		billingBean.setRemoveMult(removeMultiplier);
		billingBean.setDemandDaysPrev(demandDays);
		billingBean.setEnergyDaysPrev(energyDays);
		
		LocalDate today = now.toLocalDate();
		DateTime startOfToday = today.toDateTimeAtStartOfDay(now.getZone());
		billingBean.setEndDate(startOfToday.toDate());
		
		billingBean.setBillGroup(deviceGroupNames);
		return billingBean;
	}
	
	//Logs the parameters of this billing file export task.
	private void logStart() {
		log.info("Starting scheduled file export for schedule \"" + name + "\"");
		log.debug("Format: " + FileFormatTypes.getFormatType(fileFormatId));
		log.debug("Remove multiplier: " + removeMultiplier);
		log.debug("Demand days previous: " + demandDays);
		log.debug("Energy days previous: " + energyDays);
		log.debug("Device groups: " + deviceGroupNames.toString());
	}
}
