package com.cannontech.web.scheduledFileExport.service;

import java.util.List;

import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.scheduledFileExport.ScheduledBillingJobData;

/**
 * Service for scheduling multiple types of file export and retrieving
 * data related to scheduled file exports.
 */
public interface ScheduledFileExportService {
	
	/**
	 * Schedule a new file export as defined in the ScheduledFileExportData.
	 * @return The scheduled YukonJob for this file export.
	 */
	YukonJob scheduleFileExport(ScheduledFileExportData data, YukonUserContext userContext);
	
	/**
	 * Update the ScheduledFileExport with the specified jobId, using the specified data object.
	 * @return the scheduled YukonJob for this file export.
	 */
	YukonJob updateFileExport(ScheduledFileExportData data, YukonUserContext userContext, int jobId);
	
	/**
	 * @return all jobs based on the ScheduledBillingFileExportTask.
	 */
	public List<ScheduledRepeatingJob> getBillingExportJobs();
	
	/**
	 * @return all jobs based on the ScheduledArchivedDataFileExportTask.
	 */
	public List<ScheduledRepeatingJob> getArchivedDataExportJobs();
	
	/**
	 * Delete all Archived Data Export jobs that use the specified formatId.
	 * @return the number of jobs deleted.
	 */
	public int deleteAdeJobsByFormatId(int formatId);
	
	/**
	 * Delete all billing jobs that use the specified formatIdl
	 * @return the number of jobs deleted.
	 */
	public int deleteBillingJobsByFormatId(int formatId);
	
	/**
	 * Creates a billing job data object for the specified job.
	 */
	public ScheduledBillingJobData getBillingJobData(ScheduledRepeatingJob job);
}
