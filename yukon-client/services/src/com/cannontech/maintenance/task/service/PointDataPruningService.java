package com.cannontech.maintenance.task.service;

import org.joda.time.Instant;

public interface PointDataPruningService {

    /**
     * Delete point data
     * @return number of records deleted
     */
    int deletePointData(Instant processEndTime) ;
    
    /**
     * Delete duplicate point data.
     * @return number of records deleted
     */
    int deleteDuplicatePointData(Instant processEndTime);
}
