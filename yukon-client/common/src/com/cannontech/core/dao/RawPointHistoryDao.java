package com.cannontech.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.point.PointType;
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
    
    public enum Order {
        FORWARD, REVERSE
    }
    
    public enum OrderBy {
        TIMESTAMP, VALUE
    }

    /**
     * Consider using Range<T> (which includes booleans for this) for the associated values instead of this enum.
     */
    public enum Clusivity {
        INCLUSIVE_EXCLUSIVE(true, false),
        EXCLUSIVE_INCLUSIVE(false, true),
        INCLUSIVE_INCLUSIVE(true, true),
        EXCLUSIVE_EXCLUSIVE(false, false),
        ;
        
        private final boolean startInclusive;
        private final boolean endInclusive;

        private Clusivity(boolean startInclusive, boolean endInclusive) {
            this.startInclusive = startInclusive;
            this.endInclusive = endInclusive;
            
        }

        public boolean isStartInclusive() {
            return startInclusive;
        }

        public boolean isEndInclusive() {
            return endInclusive;
        }

        public <T extends Comparable<? super T>> Range<T> makeRange(T start, T end) {
            return new Range<T>(start, startInclusive, end, endInclusive);
        }

        public static Clusivity getClusivity(boolean startInclusive, boolean endInclusive) {
            if (startInclusive && !endInclusive) {
                return INCLUSIVE_EXCLUSIVE;
            } else if (!startInclusive && endInclusive) {
                return EXCLUSIVE_INCLUSIVE;
            } else if (startInclusive && endInclusive) {
                return INCLUSIVE_INCLUSIVE;
            } else {
                return EXCLUSIVE_EXCLUSIVE;
            }
        }
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
     * @param clusivity - determines whether each end of range is inclusive or exclusive
     * @param order - controls ordering by timestamp and changeid
     * @return List of values for the point
     */
    public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate, Clusivity clusivity, Order order);
    
    /**
     * Method to get a list of point values for a given set of pointIds and time period.  
     * @param pointIds - Id's of points to get values for
     * @param startDate - Start time of period (this is always the first argument in SQL, either > or >=)
     * @param stopDate - End time of period (this is always the second argument in SQL, either < or <=)
     * @param excludeDisabledPaos - True if disabled PAOs should be omitted from the result 
     * @param clusivity - determines whether each end of range is inclusive or exclusive
     * @param order - controls ordering by timestamp and changeid
     * @return List of values for the point
     */
    public List<PointValueHolder> getPointData(Set<Integer> pointIds, Date startDate, Date stopDate, boolean excludeDisabledPaos, Clusivity clusivity, Order order);

    /**
     * Method to get a list of point values for a given point and time period, 
     * but only returning up to maxRows rows. 
     * To return a list with maxRows closest to startDate, use reverseOrder of false.
     * To return a list with maxRows closest to stopDate, use reverseOrder of true. 
     * @param pointId - Id of point to get values for
     * @param startDate - Start time of period (this is always the first argument in SQL, either > or >=)
     * @param stopDate - End time of period (this is always the second argument in SQL, either < or <=)
     * @param clusivity - determines whether each end of range is inclusive or exclusive
     * @param order - controls ordering by timestamp and changeid
     * @param maxRows - Maximum number of rows to return
     * @return List of values for the point
     */
    public List<PointValueHolder> getLimitedPointData(int pointId, Date startDate, Date stopDate, Clusivity clusivity, boolean excludeDisabledPaos, Order order, int maxRows);
    
    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned
     * as a ListMultimap such that the RPH values for each PAO will be accessible (and ordered) on their own.
     * 
     * @param paos The Iterable of PAOs
     * @param attribute The Attribute to return, this can either be a regular or a mapped attribute
     * @param startDate The lower limit for the timestamp of the values to return, may be null
     * @param stopDate The upper limit for the timestamp of the values to return, may be null
     * @param excludeDisabledPaos True if disabled PAOs should be omitted from the result
     * @param clusivity - determines whether each end of range is inclusive or exclusive
     * @param order - controls ordering by timestamp (only affects the iteration order of the values)
     * @return
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(Iterable<? extends YukonPao> paos,
        Attribute attribute, Date startDate, Date stopDate, boolean excludeDisabledPaos, Clusivity clusivity,
        Order order);

    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned
     * as a ListMultimap such that the RPH values for each PAO will be accessible (and ordered) on their own.
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(Iterable<? extends YukonPao> paos,
        Attribute attribute, Range<Instant> dateRange, Range<Long> changeIdRange, boolean excludeDisabledPaos,
        Order order);

    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned
     * as a ListMultimap such that the RPH values for each PAO will be accessible (and ordered) on their own.
     * 
     * @param paos The Iterable of PAOs
     * @param attribute The Attribute to return, this can either be a regular or a mapped attribute
     * @param changeIdRange The change id range we will use when getting the point value data.
     * @param excludeDisabledPaos True if disabled PAOs should be omitted from the result
     * @param clusivity - determines whether each end of range is inclusive or exclusive
     * @param order - controls ordering by timestamp (only affects the iteration order of the values)
     * @return
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeDataByChangeIdRange(Iterable <? extends YukonPao> paos, Attribute attribute, Range<Long> changeIdRange, boolean excludeDisabledPaos, Clusivity clusivity, Order order);
    
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
     * @param clusivity - determines whether each end of range is inclusive or exclusive
     * @param order - controls ordering by timestamp (only affects the iteration order of the values)
     * @return
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(Iterable<? extends YukonPao> paos, Attribute attribute, Date startDate, Date stopDate, int maxRows, boolean excludeDisabledPaos, Clusivity clusivity, Order order);
    
    /**
     * This method returns RawPointHistory data for a list of PAOs and a list of Attributes. This data will be returned as a ListMultimap
     * such that the RPH values for each PAO will be accessible (and ordered) on their own. For any pao in "paos", the following will
     * be true:
     * 
     * result.get(pao).size() <= maxRows
     * 
     * @param paos The Iterable of PAOs
     * @param attribute The Iterable of Attributes to return, these can either be regular or mapped attributes
     * @param startDate The lower limit for the timestamp of the values to return, may be null
     * @param stopDate The upper limit for the timestamp of the values to return, may be null
     * @param maxRows The maximum number of rows to return for each PAO
     * @param excludeDisabledPaos True if disabled PAOs should be omitted from the result
     * @param clusivity - determines whether each end of range is inclusive or exclusive
     * @param order - controls ordering by timestamp (only affects the iteration order of the values)
     * @return
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(Iterable<? extends YukonPao> displayableDevices,
                                                                                        Iterable<Attribute> attributes,
                                                                                        Date startDate,
                                                                                        Date stopDate,
                                                                                        int maxRows,
                                                                                        boolean excludeDisabledPaos,
                                                                                        Clusivity clusivity,
                                                                                        Order order);
    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned as
     * a ListMultimap such that the RPH values for each PAO will be accessible (and ordered) on their own. For any pao
     * in "paos", the following will be true:
     * 
     * result.get(pao).size() <= maxRows
     * 
     * @param paos The Iterable of PAOs
     * @param attribute The Attribute to return, this can either be a regular or a mapped attribute
     * @param dateRange The date range over which data should be retrieved.
     * @param changeIdRange A range of changeIds to include.  This can be null (which will not filter
     *            on changeId).
     * @param maxRows The maximum number of rows to return for each PAO
     * @param excludeDisabledPaos True if disabled PAOs should be omitted from the result
     * @param order - controls ordering  [ASC, DESC]
     * @param orderBy - controls field to order by  [timestamp, value]
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(
        Iterable<? extends YukonPao> paos, Attribute attribute, Range<Instant> dateRange,
        Range<Long> changeIdRange, int maxRows, boolean excludeDisabledPaos, Order order, OrderBy orderBy);
    
    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned as
     * a ListMultimap such that the RPH values for each PAO will be accessible (and ordered) on their own. For any pao
     * in "paos", the following will be true:
     * 
     * result.get(pao).size() <= maxRows
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByPointName(Iterable<PaoIdentifier> paos,
        String pointName, Range<Instant> dateRange, Range<Long> changeIdRange, int maxRows, Order order);
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByPointName(Iterable<PaoIdentifier> paos,
        String pointName, Date startDate, Date stopDate, int maxRows, Clusivity clusivity, Order order);

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByPointName(Iterable<PaoIdentifier> paos,
        String pointName, Range<Instant> dateRange, Range<Long> changeIdRange, Order order);
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByPointName(Iterable<PaoIdentifier> paos,
        String pointName, Date startDate, Date stopDate, Clusivity clusivity, Order order);

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByTypeAndOffset(
        Iterable<PaoIdentifier> paos, PointType pointType, int offset, Range<Instant> dateRange,
        Range<Long> changeIdRange, int maxRows, Order order);
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByTypeAndOffset(
        Iterable<PaoIdentifier> paos, PointType pointType, int offset, Date startDate, Date stopDate, int maxRows,
        Clusivity clusivity, Order order);

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByTypeAndOffset(Iterable<PaoIdentifier> paos,
        PointType pointType, int offset, Range<Instant> dateRange, Range<Long> changeIdRange, Order order);
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByTypeAndOffset(Iterable<PaoIdentifier> paos,
        PointType pointType, int offset, Date startDate, Date stopDate, Clusivity clusivity, Order order);

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByDefaultPointName(
        Iterable<PaoIdentifier> paos, String defaultPointName, Range<Instant> dateRange, Range<Long> changeIdRange,
        int maxRows, Order order);
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByDefaultPointName(
        Iterable<PaoIdentifier> paos, String defaultPointName, Date startDate, Date stopDate, int maxRows,
        Clusivity clusivity, Order order);

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByDefaultPointName(Iterable<PaoIdentifier> paos,
        String defaultPointName, Range<Instant> dateRange, Range<Long> changeIdRange, Order order);
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByDefaultPointName(Iterable<PaoIdentifier> paos,
        String defaultPointName, Date startDate, Date stopDate, Clusivity clusivity, Order order);

    /**
     * Equivalent to calling
     *   getLimitedAttributeData(displayableDevices, attribute, null, null, 1, excludeDisabledDevices, false, true);
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
    * Gets values immediately before and after (with respect to timestamp) of a given point value.
    * Notes: 
    * Implemented by making two calls to getLimitedPointData().
    */
   public AdjacentPointValues getAdjacentPointValues(PointValueHolder pvh);
   
   /**
    * Delete a row of RawPointHistory by ChangeId.
    * @param changeId
    */
   public void deleteValue(int changeId);
   
   /**
    * Get the largest changeId currently in RawPointHistory
    */
   public long getMaxChangeId();
   
   public static class AdjacentPointValues {
       private PointValueHolder preceding;
       private PointValueHolder succeeding;
       public AdjacentPointValues(PointValueHolder preceding, PointValueHolder succeeding) {
           this.preceding = preceding;
           this.succeeding = succeeding;
       }
       public PointValueHolder getPreceding() {
           return preceding;
       }
       public PointValueHolder getSucceeding() {
           return succeeding;
       }
   }

}