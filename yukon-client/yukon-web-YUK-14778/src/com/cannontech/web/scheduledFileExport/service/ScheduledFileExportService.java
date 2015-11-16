package com.cannontech.web.scheduledFileExport.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;

/**
 * Service for scheduling multiple types of file export and retrieving
 * data related to scheduled file exports.
 */
public interface ScheduledFileExportService {

    /**
     * Schedule a new file export as defined in the ScheduledFileExportData.
     * @return The scheduled YukonJob for this file export.
     */
    YukonJob scheduleFileExport(ScheduledFileExportData data, YukonUserContext userContext, HttpServletRequest request);

    /**
     * Update the ScheduledFileExport with the specified jobId, using the specified data object.
     * @return the scheduled YukonJob for this file export.
     */
    YukonJob updateFileExport(ScheduledFileExportData data, YukonUserContext userContext, HttpServletRequest request, int jobId);

    /**
     * @return all non-deleted jobs of the specified ScheduledExportType
     */
    List<ScheduledRepeatingJob> getJobsByType(ScheduledExportType type);

    /**
     * Delete all Archived Data Export jobs that use the specified formatId.
     * @return the number of jobs deleted.
     */
    int deleteAdeJobsByFormatId(int formatId);

    /**
     * Delete all billing jobs that use the specified format Id
     * @return the number of jobs deleted.
     */
    int deleteBillingJobsByFormatId(int formatId);

    /**
     * Creates an export job data object for the specified job (for display in a
     * scheduledFileExportJobs tag).
     */
    ScheduledFileExportJobData getExportJobData(ScheduledRepeatingJob job);

    List<ScheduledFileExportJobData> getScheduledFileExportJobData(ScheduledExportType scheduleType);
}
