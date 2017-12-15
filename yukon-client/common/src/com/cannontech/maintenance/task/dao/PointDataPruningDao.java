package com.cannontech.maintenance.task.dao;

import org.joda.time.Instant;

public interface PointDataPruningDao {
    /**
     * Deletes records from RPH and returns number of deleted record
     * 
     * @param deleteUpto - Date upto which the records should be deleted.
     * 
     **/
    int deletePointData(Instant deleteUpto);
}
