package com.cannontech.web.stars.scheduledDataImport.dao;

import org.joda.time.Instant;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.scheduledFileImport.ScheduleImportHistoryEntry;
import com.cannontech.common.search.result.SearchResults;

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

}
