package com.cannontech.maintenance.task.service;

import org.joda.time.Instant;

public interface PointDataPruningService {

    /*
     * Delete point data
     */
    int deletePointData(Instant processEndTime) ;

    /*
     * Delete point data
     */
    int deletePointDataSql(Instant processEndTime);
    
    /*
     * Delete duplicate point data.
     */
    int deleteDuplicatePointData(Instant processEndTime);
}
