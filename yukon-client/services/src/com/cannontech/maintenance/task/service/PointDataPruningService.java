package com.cannontech.maintenance.task.service;

import org.joda.time.Instant;

public interface PointDataPruningService {

    /*
     * Delete point data
     */
    void deletePointData(Instant processEndTime);

    /*
     * Delete point data
     */
    void deletePointDataSql(Instant processEndTime);
}
