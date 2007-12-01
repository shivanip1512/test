package com.cannontech.core.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.core.dynamic.PointValueHolder;

/**
 * Data access object for raw point history values.
 * 
 * All of the methods in this DAO have the special behavior of
 * accepting the start and stop dates in any order. If start is less than
 * stop, the list will be ordered so that earlier values come first. If
 * stop is less than start, the list will be ordered so that later values 
 * come first (i.e. descending).
 */
public interface RawPointHistoryDao {

    public enum Mode {
        HIGHEST,LAST
    }

    /**
     * Method to get a point value for a given time.
     * 
     * See note about start and stop order.
     * 
     * @param pointId - Id of point to get values for
     * @param startDate - Start of time period
     * @param stopDate - End of time period
     * @return List of values for the point
     */
    public PointValueHolder getPointData(int pointId, Date date);
    
    
    /**
     * Method to get a list of point values for a given time period.
     * 
     * See note about start and stop order.
     * 
     * @param pointId - Id of point to get values for
     * @param startDate - Start of time period
     * @param stopDate - End of time period
     * @return List of values for the point
     */
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate);
    
    /**
     * Method to get a list of point values for a given time period, but only returning
     * up to maxRows rows. If startDate.before(stopDate) is true, the returned list
     * will contain the maxRows closset to startDate.  If stopDate.before(startDate) 
     * is true, the returned list will contain the maxRows closset to stopDate. 
     *
     * See note about start and stop order.
     * 
     * @param pointId - Id of point to get values for
     * @param startDate - Start of time period
     * @param stopDate - End of time period
     * @param maxRows - Maximum number of rows to return
     * @return List of values for the point
     */
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate, int maxRows);
    

    /**
     * This method gets values from raw point history similarly to getPointData. But, it
     * will not return all of the data between startDate and stopDate. Instead it will
     * return at most one value per ChartInterval. The method for determining which value
     * will be used is controlled by the Mode parameter.
     * 
     *   Mode.HIGHEST - The point data with the largest value for the period will be returned
     *   Mode.LAST - The point data with the largest timestamp for the period will be returned
     *   
     * See note about start and stop order.
     * 
     * @param pointId
     * @param startDate
     * @param stopDate
     * @param resolution
     * @param mode
     * @return
     */
    public List<PointValueHolder> getIntervalPointData(int pointId, Date startDate, Date stopDate, ChartInterval resolution, Mode mode);

}