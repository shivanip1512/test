package com.cannontech.maintenance.task.dao;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;

public interface PointDataPruningDao {
    /**
     * Deletes records from RPH and returns number of deleted record
     * 
     * @param deleteUpto - Date upto which the records should be deleted.
     * 
     **/
    int deletePointData(Instant deleteUpto);

    /**
     * Method Deletes duplicate records from RPH for given date range
     * 
     * @param dateRange : Date Range for which records have to be deleted
     * @param noLockRequired : NOLOCK will be added to the query if it is true
     * @return no of records deleted for the given date range.
     */
    int deleteDuplicatePointData(Range<Instant> dateRange, boolean noLockRequired);
}
