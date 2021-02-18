package com.cannontech.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joda.time.Instant;
import org.joda.time.LocalTime;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.point.PointType;
import com.cannontech.services.systemDataPublisher.service.model.DataCompletenessSummary;
import com.google.common.collect.ImmutableSet;
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
     * @param instantRange - time period, also defines clusivity
     * @param order - controls ordering by timestamp and changeid
     * @return List of values for the point
     */
    public List<PointValueHolder> getPointData(int pointId, Range<Instant> instantRange, Order order);

    /**
     * Method to get a list of point values for a given set of pointIds and time period.
     * @param pointIds - Id's of points to get values for
     * @param range The date range over which data should be retrieved.
     * @param excludeDisabledPaos - True if disabled PAOs should be omitted from the result
     * @param order - controls ordering by timestamp and changeid
     * @return List of values for the point
     */
    public List<PointValueHolder> getPointData(Set<Integer> pointIds, ReadableRange<Instant> range, boolean excludeDisabledPaos, Order order);

    /**
     * Method to get a list of point values for a given point and time period,
     * but only returning up to maxRows rows.
     * To return a list with maxRows closest to startDate, use Order.Forward.
     * To return a list with maxRows closest to stopDate, use Order.Reverse.
     * @param pointId - Id of point to get values for
     * @param instantRange - time period, also defines clusivity
     * @param order - controls ordering by timestamp and changeid
     * @param maxRows - Maximum number of rows to return
     * @return List of values for the point
     */
    public List<PointValueHolder> getLimitedPointData(int pointId, Range<Instant> instantRange, boolean excludeDisabledPaos, Order order, int maxRows);

    /**
     * Method to get a list of point values for a given point and time period,
     * but only returning up to maxRows rows.
     * To return a list with maxRows closest to startDate, use Order.Forward.
     * To return a list with maxRows closest to stopDate, use Order.Reverse.
     * @param pointId - Id of point to get values for
     * @param instantRange - time period, also defines clusivity
     * @param order - controls ordering by orderBy
     * @param orderBy - controls the data to order respective of (value or timestamp)
     * @param maxRows - Maximum number of rows to return
     * @return List of values for the point
     */
    public List<PointValueHolder> getLimitedPointData(int pointId, Range<Instant> instantRange, boolean excludeDisabledPaos, Order order, OrderBy orderBy, int maxRows);
    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned
     * as a ListMultimap such that the RPH values for each PAO will be accessible (and ordered) on their own.
     *
     * @param paos The Iterable of PAOs
     * @param attribute The Attribute to return, this can either be a regular or a mapped attribute
     * @param excludeDisabledPaos True if disabled PAOs should be omitted from the result
     * @param instantRange - time period, also defines clusivity
     * @param order - controls ordering by timestamp (only affects the iteration order of the values)
     * @param excludeQualities - if not null and not empty, rows with point qualities in this set will be discarded
     * @return
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(Iterable<? extends YukonPao> paos,
        Attribute attribute,
        boolean excludeDisabledPaos,
        Range<Instant> instantRange,
        Order order,
        Set<PointQuality> excludeQualities);

    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned
     * as a ListMultimap such that the RPH values for each PAO will be accessible (and ordered) on their own.
     * Only returns data with a value greater than the specified minimum value.
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(Iterable<? extends YukonPao> paos,
        Attribute attribute,
        ReadableRange<Instant> dateRange,
        ReadableRange<Long> changeIdRange,
        boolean excludeDisabledPaos,
        Order order,
        Double minimumValue,
        Set<PointQuality> excludeQualities);

    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute for a specific time. 
     * Only returns data with a value greater than the specified minimum value. 
     * 
     * @return ListMultimap containing RPH values for each PAO
     */
    ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(Iterable<? extends YukonPao> paos,
        Attribute attribute, ReadableRange<Instant> dateRange, LocalTime time, ReadableRange<Long> changeIdRange,
        boolean excludeDisabledPaos, Order order, Double minimumValue, Set<PointQuality> excludeQualities);
    
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
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeDataByChangeIdRange(Iterable <? extends YukonPao> paos, Attribute attribute, ReadableRange<Long> changeIdRange, boolean excludeDisabledPaos, Range<Instant> instantRange, Order order);

    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned as a ListMultimap
     * such that the RPH values for each PAO will be accessible (and ordered) on their own. For any pao in "paos", the following will
     * be true:
     *
     * result.get(pao).size() <= maxRows
     *
     * @param paos The Iterable of PAOs
     * @param attribute The Attribute to return, this can either be a regular or a mapped attribute
     * @param maxRows The maximum number of rows to return for each PAO
     * @param excludeDisabledPaos True if disabled PAOs should be omitted from the result
     * @param instantRange - time period, also defines clusivity
     * @param order - controls ordering by timestamp (only affects the iteration order of the values)
     * @param excludeQualities - if not null and not empty, rows with point qualities in this set will be discarded
     * @return
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(Iterable<? extends YukonPao> paos,
            Attribute attribute,
            int maxRows,
            boolean excludeDisabledPaos,
            Range<Instant> range,
            Order order,
            Set<PointQuality> excludeQualities);

    /**
     * This method returns RawPointHistory data for a list of PAOs and a list of Attributes. This data will be returned as a ListMultimap
     * such that the RPH values for each PAO will be accessible (and ordered) on their own. For any pao in "paos", the following will
     * be true:
     *
     * result.get(pao).size() <= maxRows
     *
     * @param paos The Iterable of PAOs
     * @param attribute The Iterable of Attributes to return, these can either be regular or mapped attributes
     * @param dateRange The date range over which data should be retrieved.
     * @param maxRows The maximum number of rows to return for each PAO
     * @param excludeDisabledPaos True if disabled PAOs should be omitted from the result
     * @param order - controls ordering by timestamp (only affects the iteration order of the values)
     * @param excludeQualities - if not null and not empty, rows with point qualities in this set will be discarded
     * @return
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(Iterable<? extends YukonPao> displayableDevices,
                                                                                        Iterable<? extends Attribute> attributes,
                                                                                        ReadableRange<Instant> dateRange,
                                                                                        int maxRows,
                                                                                        boolean excludeDisabledPaos,
                                                                                        Order order,
                                                                                        Set<PointQuality> excludeQualities);
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
     * @param excludeQualities - if not null and not empty, rows with point qualities in this set will be discarded
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(
        Iterable<? extends YukonPao> paos,
        Attribute attribute,
        ReadableRange<Instant> dateRange,
        ReadableRange<Long> changeIdRange,
        int maxRows,
        boolean excludeDisabledPaos,
        Order order,
        OrderBy orderBy,
        Set<PointQuality> excludeQualities);

    /**
     * This method returns RawPointHistory data for a list of PAOs and a given Attribute. This data will be returned as
     * a ListMultimap such that the RPH values for each PAO will be accessible (and ordered) on their own. For any pao
     * in "paos", the following will be true:
     *
     * result.get(pao).size() <= maxRows
     */
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByPointName(Iterable<PaoIdentifier> paos,
        String pointName, ReadableRange<Instant> dateRange, ReadableRange<Long> changeIdRange, int maxRows, Order order);

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByPointName(Iterable<PaoIdentifier> paos,
        String pointName, ReadableRange<Instant> dateRange, ReadableRange<Long> changeIdRange, Order order);

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByTypeAndOffset(
        Iterable<PaoIdentifier> paos, PointType pointType, int offset, ReadableRange<Instant> dateRange,
        ReadableRange<Long> changeIdRange, int maxRows, Order order);

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByTypeAndOffset(Iterable<PaoIdentifier> paos,
        PointType pointType, int offset, ReadableRange<Instant> dateRange, ReadableRange<Long> changeIdRange, Order order);

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedDataByDefaultPointName(
        Iterable<PaoIdentifier> paos, String defaultPointName, ReadableRange<Instant> dateRange, ReadableRange<Long> changeIdRange,
        int maxRows, Order order);

    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getDataByDefaultPointName(Iterable<PaoIdentifier> paos,
        String defaultPointName, ReadableRange<Instant> dateRange, ReadableRange<Long> changeIdRange, Order order);

    /**
     * Equivalent to calling
     *   getLimitedAttributeData(displayableDevices, attribute, null, null, 1, excludeDisabledDevices, false, true);
     *
     * and converting the Multimap into a Map (because there will only be one value per key).
     */
    public Map<PaoIdentifier, PointValueQualityHolder> getSingleAttributeData(Iterable<? extends YukonPao> paos,
            Attribute attribute,
            boolean excludeDisabledPaos,
            Set<PointQuality> excludeQualities);

    /**
     * Equivalent to calling
     * getLimitedAttributeData(displayableDevices, attribute, null, changeIdRange, 1, excludeDisabledDevices,
     * false, true);
     *
     * and converting the Multimap into a Map (because there will only be one value per key).
     */
    Map<PaoIdentifier, PointValueQualityHolder> getSingleAttributeData(Iterable<? extends YukonPao> displayableDevices,
            Attribute attribute, boolean excludeDisabledPaos, Set<PointQuality> excludeQualities,
            ReadableRange<Long> changeIdRange);

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
    public void changeQuality(long changeId, PointQuality questionable);

    /**
     * Get a PointValueQualityHolder for a particular changeId.
     * @param changeId
     * @return
     */
    public PointValueQualityHolder getPointValueQualityForChangeId(long changeId);

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
   public void deleteValue(long changeId);

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

   /**
    * Gets the value of a point for an exact time stamp or throws {@link NotFoundException}
    * if no value exists for that point in time
    */
   public PointValueQualityHolder getSpecificValue(int pointId, long timestamp) throws NotFoundException;
   
   /**
    * Method to get a list of point values for a given point and time period and add it to queue.
    * @param pointId - Id of point to get values for
    * @param instantRange - time period, also defines clusivity
    * @param order - controls ordering by timestamp and changeid
    * @param queue - Queue in which data has to be added
    * @param isCompleted - Flag indicating queuing completion.
    * @return List of values for the point
    */
   
   void queuePointData(int pointId, Range<Instant> instantRange, Order order,
        BlockingQueue<PointValueHolder> queue, AtomicBoolean isCompleted);

    /**
     * Method to get a list of point values for a given point and time period (it includes the point values for disabled paos).
     * StartDate is always exclusive, stopDate is inclusive.
     * Ordering is always timestamp asc, changeid asc
     * @param pointId - Id of point to get values for
     * @param startDate - Start time of period (this is always the first argument in SQL, either > or >=)
     * @param stopDate - End time of period (this is always the second argument in SQL, either < or <=)
     * @return List of values for the point
     */
    List<PointValueHolder> getPointDataWithDisabledPaos(int pointId, Date startDate, Date stopDate);

    /**
     * Deletes an entry from RPH by point id, value and timestamp.
     */
    void deletePointData(int pointId, double value, Instant timestamp);
    
    /**
     * This method returns RawPointHistory data for a list of PAOs, a given Attribute and value
     * and converting the Multimap into a Map (because there will only be one value per key).
     */
    Map<PaoIdentifier, PointValueQualityHolder> getMostRecentAttributeDataByValue(
            Iterable<? extends YukonPao> paos, Attribute attribute, boolean excludeDisabledPaos, int value,
            Set<PointQuality> excludeQualities);

    /**
     * This method returns the object consisting of record count for each PAO (paoCount) for devices reported every hour within the date range
     * @param deviceGroup - Device group 
     * @param dateRange - range of days in which device reported every hour  
     * @param paoType - Set of PAO Types applicable for device Group
     * @return DataCompletenessSummary
     */
    DataCompletenessSummary getDataCompletenessRecords(DeviceGroup deviceGroup, Range<Date> dateRange, ImmutableSet<PaoType> paoType);

    /**
     * Similar to getLimitedAttributeData, but used for "summed" data, where multiple values over the range are summed 
     * together per pao.
     */
    ListMultimap<PaoIdentifier, PointValueQualityHolder> getSummedAttributeData(Iterable<? extends YukonPao> paos, 
            Attribute attribute, ReadableRange<Instant> dateRange, boolean excludeDisabledPaos, 
            Set<PointQuality> excludeQualities);
}