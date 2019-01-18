/**
 * Interface TrendDataService
 * <p>
 * Trend Data Service provides for the most part factories for graph data, as
 * well as provide data binding specific to Graph Data.
 
 * 
 * @author      Thomas Red-Cloud
 * @email       ThomasRedCloud@Eaton.com
 * @version     %I%, %G%
 * @since       1.0
 */
package com.cannontech.web.tools.trends.service;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.common.util.Range;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.user.YukonUserContext;

public interface TrendDataService {
 
    /**
     * rawPointHistoryDataProvider
     * return a consumable <code>List<PointValueHolder></code>
     * This is a simple dataProvider with RawPointHistoryDao injected {@link RawPointHistoryDao#getPointData(int, Range, Order)}. 
     * It takes in the unique identifier for the series, the interface RawPointHistoryDao and returns back a list of value pairs. 
     * @see {@link RawPointHistoryDao#getPointData(int, java.util.Date, java.util.Date)
     * <p>
     * @param pointId {@link int} is the unique identifier for the range of entries.
     * @param rawPointHistoryDao {@link RawPointHistoryDao} is dependency service the client object needs to provide the data back.   
     * @return {@link PointValueHolder} <code>List<PointValueHolder></code>
     */
    List<PointValueHolder> rawPointHistoryDataProvider(int pointId);
    
    /**
     * requestPeakDateDataProvider
     * return a consumable <code>List<PointValueHolder></code>
     * This is a dataProvider that requests datetime stamp of the highest peak from within the stream of points. Its intent
     * to render a Peak Graph. It takes in the unique identi
     * fier for the series and the start date and instance of 
     * {@link RawHistoryPointDao} to make the request. 
     * @see {@link #datePointHistoryDataProvider(int, DateTime, DateTime)}
     * @see {@link #dateGraphDataProvider(List, DateTime, ReadableInstant)}
     * @see {@link RawPointHistoryDao#getPointData(int, java.util.Date, java.util.Date)
     * <p>
     * @param pointId {@link int} is the unique identifier for the range of entries.
     * @param startDate {@link ReadableInstant} The timestamp of where to start the scan for peak -> limit of data.
     * @param userContext {@YukonUserContext} The user to adjust the peak date to
     * @param rawPointHistoryDao {@link RawPointHistoryDao} is dependency service the client object needs to provide the data back.
     * @return {@link DateTime} the peak date, adjusted to userContext
     */
    DateTime requestPeakDateDataProvider(int pointId, Date startDate, YukonUserContext userContext);
    
    /**
     * datePointHistoryDataProvider
     * return a consumable <code>List<PointValueHolder></code>
     * This is a data provider intent for Date Graph. 
     * It takes in the unique identifier for the series, a specific date and end date. 
     * @see {@link #requestPeakDateDataProvider(int, ReadableInstant)} 
     * @see {@link #dateGraphDataProvider(List, DateTime, ReadableInstant)}
     * @see {@link RawPointHistoryDao#getPointData(int, java.util.Date, java.util.Date)
     * <p>
     * @param pointId {@link int} is the unique identifier for the range of entries.
     * @param specificDate {@link DateTime} is the timestamp of where to take the snapshot of data and record the points to build with.
     * @param endDate {@link DateTime} is the timestamp of where to stop taking the snapshot of data, and begin the loop for fill of the trend line.
     * @param rawPointHistoryDao {@link RawPointHistoryDao} is dependency service the client object needs to provide the data back.
     * @return {@link List<PointValueHolder>} 
     */
    List<PointValueHolder> datePointHistoryDataProvider(int pointId, DateTime specificDate, DateTime endDate);
    
    /**
     * yesterdayGraphDataProvider
     * return a consumable list of Object<long, double>
     * This data provideder takes the journal of data points and graphs the trend occuring the next day.
     * @see {@link #dateGraphDataProvider(List<PointValueHolder>, DateTime, ReadableInstant)}
     * @see {@link #graphDataProvider(List<PointValueHolder>)}
     * @see {@link #usageGraphDataProvider(List<PointValueHolder>)
     * <p>
     * @param data {@link List<PointValueHolder>} is the unique identifier for the range of entries.
     * @return <code>List<Object[]></code>
     */
    List<Object[]> yesterdayGraphDataProvider(List<PointValueHolder> data);
    
    /**
     * usageGraphDataProvider
     * return a consumable list of Object<long, double>
     * This data provider does a transform where m = yd - yp,  yp = m for the next point and provides the data for the
     * trendline.
     * @see {@link #dateGraphDataProvider(List<PointValueHolder>, DateTime, ReadableInstant)}
     * @see {@link #graphDataProvider(List<PointValueHolder>)}
     * @see {@link #usageGraphDataProvider(List<PointValueHolder>)
     * <p>
     * @param data {@link List<PointValueHolder>} is the unique identifier for the range of entries.
     * @return <code>List<Object[]></code>
     */
    List<Object[]> usageGraphDataProvider(List<PointValueHolder> data);
    
    /**
     * dateGraphDataProvider
     * return a consumable list of Object<long, double>
     * This data provider takes a List<PointValueHolder>, a start and ending for loop filling the trend line with the repeated data.
     * The call returns a List<Object[]> of data.
     * @see {@link #dateGraphDataProvider(List<PointValueHolder>, DateTime, ReadableInstant)}
     * @see {@link #graphDataProvider(List<PointValueHolder>)}
     * @see {@link #usageGraphDataProvider(List<PointValueHolder>)
     * <p>
     * @param data {@link List<PointValueHolder>} is the unique identifier for the range of entries.
     * @param chartDatePrime{@link DateTime}
     * @param chartDateLimit{@link ReadableInstant}
     * @return <code>List<Object[]></code>
     */
    List<Object[]> dateGraphDataProvider(List<PointValueHolder> data, DateTime chartDatePrime, ReadableInstant chartDateLimit);
    
    /**
     * graphDataProvider
     * This data provider takes a List<PointValueHolder>, and provides the points to be rendered to the trendline.
     * @see {@link #dateGraphDataProvider(List<PointValueHolder>, DateTime, ReadableInstant)}
     * @see {@link #graphDataProvider(List<PointValueHolder>)}
     * @see {@link #usageGraphDataProvider(List<PointValueHolder>)
     * <p>
     * @param data {@link List<PointValueHolder>} is the unique identifier for the range of entries.
     * @return <code>List<Object[]></code>
     */
    List<Object[]> graphDataProvider(List<PointValueHolder> data);
    
    /**
     * Gets the next available refresh time.
     * @param Instant value indicating the most recent time the data was loaded for trend chart
     * @return Instant the next refresh time
     */
    Instant getNextRefreshTime(Instant lastUpdateTime);
    

    /**
     * Gets the milliseconds for refresh time.
     * @return long the refresh time
     */
    long getRefreshMilliseconds();
}

