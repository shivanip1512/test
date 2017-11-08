package com.cannontech.maintenance.task.dao;

import org.joda.time.Instant;

public interface PointDataPruningDao {
    /**
     * Deletes records from RPH and returns number of deleted record
     * 
     * @param processEndTime - Time when this process should stop.
     * @param deleteUpto - Date upto which the records should be deleted.
     * 
     **/
    int deletePointData(Instant processEndTime, Instant deleteUpto);

    /**
     * Deletes records from RPH and returns number of deleted record
     * 
     * @param deleteUpto - Date upto which the records should be deleted.
     * 
     **/
    int deletePointData(Instant deleteUpto);
}
