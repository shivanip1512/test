package com.cannontech.web.scheduledDataImport.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.scheduledFileImport.ScheduledDataImport;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.scheduledDataImportTask.ScheduledDataImportTaskJobWrapperFactory.ScheduledDataImportTaskJobWrapper;

public interface ScheduledDataImportService {

    /**
     * Schedule a new data import job with parameters defined inside ScheduledDataImport and
     * return an YukonJob object.
     */
    YukonJob scheduleDataImport(ScheduledDataImport data, YukonUserContext userContext);

    /**
     * Update the scheduleDataImport job with the specified jobId, using the specified ScheduledDataImport
     * object
     * and return an YukonJob object.
     */
    YukonJob updateDataImport(ScheduledDataImport data, YukonUserContext userContext);

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

    /**
     * Retrieve the scheduled data import jobs with specified paging parameters and shorting parameters.
     */
    SearchResults<ScheduledDataImportTaskJobWrapper> getScheduledFileImportJobData(PagingParameters paging,
            SortingParameters sorting, YukonUserContext userContext);

    /**
     * Toggles Job enabled status. If disabled, make enabled. If enabled,
     * make disabled. Returns new state of the job.
     * 
     */
    boolean toggleEnabled(int jobId, LiteYukonUser yukonUser);

    /**
     * Starts the job with the specified Job ID.
     */
    Map<String, Object> startJob(String jobId, HttpServletRequest request);
}
