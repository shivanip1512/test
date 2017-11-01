package com.cannontech.core.dao;

import java.util.Date;

import org.joda.time.DateTime;

public interface MaintenanceDao {
    /**
     * Deletes records from RPH and returns number of deleted record
     * 
     * @param processEndTime - Time when this process should stop.
     * @param deleteUpto - Date upto which the records should be deleted.
     * 
     **/
    int archiveRph(DateTime processEndTime, Date deleteUpto);

}
