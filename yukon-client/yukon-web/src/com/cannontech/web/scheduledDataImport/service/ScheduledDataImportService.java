package com.cannontech.web.scheduledDataImport.service;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.scheduledFileImport.ScheduledDataImport;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.user.YukonUserContext;

public interface ScheduledDataImportService {

    /**
     * Schedule a new data import job with parameters defined inside ScheduledDataImport and
     * return an YukonJob object.
     */
    YukonJob scheduleDataImport(ScheduledDataImport data, YukonUserContext userContext, HttpServletRequest request);

    /**
     * Update the scheduleDataImport job with the specified jobId, using the specified ScheduledDataImport
     * object
     * and return an YukonJob object.
     */
    YukonJob updateDataImport(ScheduledDataImport data, YukonUserContext userContext, HttpServletRequest request);

    /**
     * Retrieve the scheduled data import job with the specified jobId.
     */
    ScheduledDataImport getJobById(int jobId);

    /**
     * Delete the scheduled data import job with the specified jobId.
     */
    ScheduledDataImport deleteJobById(int jobId);

    /**
     * Return the state of job with the specified jobId.
     */
    JobState getJobState(int jobId);
}
