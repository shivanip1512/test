package com.cannontech.core.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.core.dynamic.PointValueHolder;

/**
 * Data access object for raw point history values
 */
public interface RawPointHistoryDao {

    /**
     * Method to get a list of point values for a given time period
     * @param pointId - Id of point to get values for
     * @param startDate - Start of time period
     * @param stopDate - End of time period
     * @return List of values for the point
     */
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate);

}