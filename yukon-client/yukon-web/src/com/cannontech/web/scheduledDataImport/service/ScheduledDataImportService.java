package com.cannontech.web.scheduledDataImport.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.exception.FileCreationException;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.scheduledFileImport.ScheduleImportHistoryEntry;
import com.cannontech.common.scheduledFileImport.ScheduledDataImport;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.scheduledDataImportTask.ScheduledDataImportTaskJobWrapperFactory.ScheduledDataImportTaskJobWrapper;
import com.cannontech.web.stars.scheduledDataImport.dao.ScheduledDataImportDao.SortBy;

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

    /**
     * Retrieve the file import history of a job with specified jobGroupId.
     */
    SearchResults<ScheduleImportHistoryEntry> getImportHistory(int jobGroupId, Instant from, Instant to,
            SortBy sortBy, Direction direction, PagingParameters paging);

    /**
     * Returns a file object. This will be used to download both archived results
     * and archived failure results.
     * 
     * @param fileName - Name of the archived / error file.
     * @param isSuccessFile - true for downloading success file else false.
     * @param failedFilePath - Error file output path when downloading error file else not required.
     */
    public File downloadArchivedFile(String fileName, boolean isSuccessFile, String failedFilePath)
            throws FileNotFoundException, FileCreationException;

    /**
     * Retrieve the data required for downloading a success/failure file
     * 
     * @param entryID
     * @param isSuccessFile
     * @return Map<String, String>
     */
    public Map<String, String> getHistoryEntryById(int entryID, boolean isSuccessFile);

}
