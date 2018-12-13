package com.cannontech.web.scheduledDataImport.service;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.scheduledFileImport.ScheduledDataImport;
import com.cannontech.common.search.result.SearchResults;
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
     * Retrieve the scheduled data import jobs with specified paging parameters and shorting parameters.
     */
    SearchResults<ScheduledDataImportTaskJobWrapper> getScheduledFileImportJobData(PagingParameters paging,
            SortingParameters sorting, YukonUserContext userContext);

}
