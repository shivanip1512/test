package com.cannontech.core.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.google.common.collect.ListMultimap;

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
     * Method to get a list of point values for a given point and time period.
     * StartDate is always exclusive, stopDate is inclusive.
     * Ordering is always timestamp asc, changeid asc  
     * @param pointId - Id of point to get values for
     * @param startDate - Start time of period (this is always the first argument in SQL, either > or >=)
     * @param stopDate - End time of period (this is always the second argument in SQL, either < or <=)
     * @return List of values for the point
     */
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate);
    
    /**
     * Method to get a list of point values for a given point and time period.  
     * @param pointId - Id of point to get values for
     * @param startDate - Start time of period (this is always the first argument in SQL, either > or >=)
     * @param stopDate - End time of period (this is always the second argument in SQL, either < or <=)
     * @param startInclusive - When true, startDate is inclusive, stopDate is exclusive.  When false, startDate is exclusive, stopDate is inclusive
     * @param reverseOrder - When true, results are returned from query in timestamp DESC, changeId DESC order
     * @return List of values for the point
     */
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate, boolean startInclusive, boolean reverseOrder);
    
    /**
     * Method to get a list of point values for a given point and time period, 
     * but only returning up to maxRows rows. 
     * To return a list with maxRows closest to startDate, use reverseOrder of false.
     * To return a list with maxRows closest to stopDate, use reverseOrder of true. 
     * @param pointId - Id of point to get values for
     * @param startDate - Start time of period (this is always the first argument in SQL, either > or >=)
     * @param stopDate - End time of period (this is always the second argument in SQL, either < or <=)
     * @param startInclusive - When true, startDate is inclusive, stopDate is exclusive.  When false, startDate is exclusive, stopDate is inclusive
     * @param reverseOrder - When true, results are returned from query in timestamp DESC, changeId DESC order
     * @param maxRows - Maximum number of rows to return
     * @return List of values for the point
     */
    public List<PointValueHolder> getLimitedPointData(int pointId, Date startDate, Date stopDate, boolean startInclusive, boolean reverseOrder, int maxRows);
    
    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned as a ListMultimap
     * such that the RPH values for each PAO will be accessible (and ordered) on their own.
     * 
     * @param paos The Iterable of PAOs
     * @param attribute The Attribute to return, this can either be a regular or a mapped attribute
     * @param startDate The lower limit for the timestamp of the values to return, may be null
     * @param stopDate The upper limit for the timestamp of the values to return, may be null
     * @param excludeDisabledPaos True if disabled PAOs should be omitted from the result
     * @return
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(Iterable<? extends YukonPao> paos, Attribute attribute, Date startDate, Date stopDate, boolean excludeDisabledPaos);
    
    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned as a ListMultimap
     * such that the RPH values for each PAO will be accessible (and ordered) on their own. For any pao in "paos", the following will
     * be true:
     * 
     * result.get(pao).size() <= maxRows
     * 
     * @param paos The Iterable of PAOs
     * @param attribute The Attribute to return, this can either be a regular or a mapped attribute
     * @param startDate The lower limit for the timestamp of the values to return, may be null
     * @param stopDate The upper limit for the timestamp of the values to return, may be null
     * @param maxRows The maximum number of rows to return for each PAO
     * @param excludeDisabledPaos True if disabled PAOs should be omitted from the result
     * @return
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(Iterable<? extends YukonPao> paos, Attribute attribute, Date startDate, Date stopDate, int maxRows, boolean excludeDisabledPaos);
    
    /**
     * Equivalent to calling
     *   getLimitedAttributeData(displayableDevices, attribute, null, null, 1, excludeDisabledDevices);
     *   
     * and converting the Multimap into a Map (because there will only be one value per key).
     */
    public Map<PaoIdentifier, PointValueQualityHolder> getSingleAttributeData(Iterable<? extends YukonPao> paos, Attribute attribute, boolean excludeDisabledPaos);


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
     * Always excludes start date, and includes end date.
     * 
     * @param pointId
     * @param startDate
     * @param stopDate
     * @param resolution
     * @param mode
     * @return
     */
    public List<PointValueHolder> getIntervalPointData(int pointId, Date startDate, Date stopDate, ChartInterval resolution, Mode mode);

    /**
     * Update the quality for the specified change id.
     * 
     * @param changeId any valid change id
     * @param questionable any valid quality
     */
    public void changeQuality(int changeId, PointQuality questionable);
    
    /**
     * Get a PointValueQualityHolder for a particular changeId.
     * @param changeId
     * @return
     */
    public PointValueQualityHolder getPointValueQualityForChangeId(int changeId);
    
    /**
    * Gets values before or after a given changeId.
    * All values for the point that changeId references are ordered by timestamp and changeId, the those values adjacent to the one with the given changeId are returned.
    * Offsets are an array of integers that determine which adjacent points to return.
    * Examples:
    * offset = 0 => same value that changeId points to
    * offset = -1 => value previous to value that changeId points to
    * offset = 1 => value after value that changeId points to
    * The list of values returned will have the same length as the offset array passed in.
    * If an offset is out of range of the available values for the point, then null will be returned as its value.
    * @param changeId
    * @param offsets
    * @return
    * @throws SQLException
    */
   public List<PointValueQualityHolder> getAdjacentPointValues(final int changeId, int ... offsets) throws SQLException;
   
   /**
    * Gets values immediately before and after (with respect to timestamp) of a given point value.
    * Notes: 
    * Implemented by making two calls to getLimitedPointData().
    * This method seems to perform slightly better the other getImmediatelyAdjacentPointValues() method when run on MSSQL vs Oracle.
    * @param pvh
    * @return
    * @throws SQLException
    */
   public List<PointValueHolder> getImmediatelyAdjacentPointValues(PointValueHolder pvh) throws SQLException;
   
   /**
    * Gets values immediately before and after (with respect to timestamp) of a given point value and it's rph changeId.
    * Notes:
    * Implemented by using a single complex query which is not compatible with MS2000. Will degrade to use the other version of getImmediatelyAdjacentPointValues().
    * This method seems to perform slightly better than the other getImmediatelyAdjacentPointValues() method when run on Oracle vs MSSQL
    * @param pvh
    * @param changeId
    * @return
    * @throws SQLException
    */
   public List<PointValueHolder> getImmediatelyAdjacentPointValues(PointValueHolder pvh, int changeId) throws SQLException;
   
   /**
    * Delete a row of RawPointHistory by ChangeId.
    * @param changeId
    */
   public void deleteValue(int changeId);

}