package com.cannontech.web.stars.scheduledDataImport.dao;

import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.scheduledFileImport.ScheduleImportHistoryEntry;
import com.cannontech.common.search.result.SearchResults;
import com.google.common.collect.Multimap;

public interface ScheduledDataImportDao {

    public enum SortBy {
        FILENAME("FileName"),
        DATETIME("ImportDate"),
        SUCCESS("SuccessCount"),
        TOTAL("Total"),
        FAILURE("FailureCount"),
        FAILEDFILENAME("FailedFileName");

        private SortBy(String dbString) {
            this.dbString = dbString;
        }

        private final String dbString;

        public String getDbString() {
            return dbString;
        }

    }

    /**
     * Retrieve the file import history of a job with specified jobGroupId.
     */
    SearchResults<ScheduleImportHistoryEntry> getImportHistory(int jobGroupId, Instant from, Instant to,
            SortBy sortBy, Direction direction, PagingParameters paging);

    /**
     * Retrieve the data required for downloading a success/failure file
     * 
     */
    public Map<String, String> getHistoryEntryById(int entryID, boolean isSuccessFile);

    /**
     * Retrieves all entries that have an archive file, mapped by creation date.
     */
    Multimap<Instant, ScheduleImportHistoryEntry> getEntriesWithArchiveByDate();

    /**
     * Marks the DB entry as no longer having an archived file associated with it.
     * This should be done in conjunction with actually deleting the file.
     */
    boolean markArchiveDeleted(int entryId);

}
