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
     * @return array of number of records deleted and ID of last duplicate found
     */
    Integer[] deleteDuplicatePointData(Instant processEndTime);
}
