/**
 * Class TrendDataServiceImpl
 * <p>
 * TrendDataServiceImp provides consumable data factories to be used for Graph Data
 * <p>
 */
package com.cannontech.web.tools.trends.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.trends.TrendDataController;
import com.cannontech.web.tools.trends.service.TrendDataService;
@Service
public class TrendDataServiceImpl implements TrendDataService {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;

    private static final Duration MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH = Duration.standardMinutes(15);
    private static final Logger log = YukonLogManager.getLogger(TrendDataController.class);

    @Override
    public List<PointValueHolder> rawPointHistoryDataProvider(int pointId, Instant start, Instant end) {
        Range<Instant> instantRange = Range.inclusive(start, end);
        int maxRows = globalSettingDao.getInteger(GlobalSettingType.TRENDS_READING_PER_POINT);
        if (maxRows != 0) {
            List<PointValueHolder> values =
                new ArrayList<PointValueHolder>(rawPointHistoryDao.getLimitedPointData(pointId, instantRange, false,
                    Order.REVERSE, maxRows));
            Collections.reverse(values);
            return values;
        } else {
            return rawPointHistoryDao.getPointData(pointId, instantRange, Order.FORWARD);
        }

    }
    
    @Override
    public DateTime requestPeakDateDataProvider(int pointId, Date startDate, YukonUserContext userContext) {
        Range<Date> instantRange = Range.fromInclusive(startDate);
        List<PointValueHolder> peakPointValue = rawPointHistoryDao.getLimitedPointData(pointId, instantRange.translate(CtiUtilities.INSTANT_FROM_DATE), 
                                                                                       false, Order.REVERSE, OrderBy.VALUE, 1);
        
        if (!peakPointValue.isEmpty()) {
            Date peakDate = peakPointValue.get(0).getPointDataTimeStamp();
            return new DateTime(peakDate, userContext.getJodaTimeZone()).withTimeAtStartOfDay();
        } else {    //no peak found default to now...there is no data for the point anyways.
            return DateTime.now();
        }
    }

    @Override
    public List<PointValueHolder> datePointHistoryDataProvider(int pointId, DateTime specificDate, DateTime endDate) {
        Instant start = specificDate.toInstant();
        Instant end = endDate.toInstant();
        Range<Instant> instantRange = Range.inclusive(start, end);
        List<PointValueHolder> results = rawPointHistoryDao.getPointData(pointId, instantRange, Order.FORWARD);
        return results;
    }

    @Override
    public  List<Object[]> yesterdayGraphDataProvider(List<PointValueHolder> data) {
        log.debug("YesterdayGraphDataProvider Called");
        List<Object[]> values = new ArrayList<>();
        DateTime startOfTomorrow = new DateTime().withTimeAtStartOfDay().plusDays(1).plusMillis(1);
        for (PointValueHolder pvh : data) {
            Object[] value;
            DateTime timestamp = new DateTime(pvh.getPointDataTimeStamp().getTime());
            timestamp = timestamp.plusDays(1);
            // only include timestamps from yesterday that do not exceed "today".
            if (timestamp.isBefore(startOfTomorrow)) {
                value = new Object[] { timestamp.getMillis(), pvh.getValue() };
                values.add(value);
            } else {
                log.debug("Skipping: Timestamp={} Value={}", timestamp.toString(), pvh.getValue());
            }
        }
        }
        log.debug("YesterdayGraphDataProvider:Amount Returned:" + values.size());
        return values;
    }
    

    @Override
    public  List<Object[]> usageGraphDataProvider(List<PointValueHolder> data, DateTime datePrime) {
        log.debug("UsageGraphDataProvider Called");
        
        List<Object[]> values = new ArrayList<>();
        DateTime dateNow = new DateTime().withTimeAtStartOfDay().plusDays(1);
        DateTime previousTimeStamp = new DateTime();
        double previousPointValue = 0;

        boolean skipFirstPvh = true;
        for (PointValueHolder pvh : data) {
            
            double itemPointValue = pvh.getValue();
            DateTime itemTimeStamp = new DateTime(pvh.getPointDataTimeStamp().getTime());
            DateTime compareTimeStamp = itemTimeStamp.withTimeAtStartOfDay();
            
            if (skipFirstPvh) {
                // skip the first reading in the list, but capture it for comparing for the next pvh
                previousTimeStamp = compareTimeStamp;
                previousPointValue = itemPointValue;
                skipFirstPvh = false;
                continue;
            }

            boolean flagNow = itemTimeStamp.isBefore(dateNow);
            boolean flagPrime = itemTimeStamp.isAfter(datePrime);
            if (flagPrime && flagNow) {
                if (values.isEmpty() || compareTimeStamp.isAfter(previousTimeStamp)) {
                    previousTimeStamp = compareTimeStamp;
                    double mPoint = itemPointValue - previousPointValue;
                    Object[] value = new Object[] { itemTimeStamp.getMillis(), mPoint };
                    previousPointValue = itemPointValue;
                    values.add(value);
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }
        log.debug("UsageGraphDataProvider:Amount Returned:" + values.size());
        return values;
    }

    @Override
    public List<Object[]> dateGraphDataProvider(List<PointValueHolder> data, DateTime chartDatePrime, ReadableInstant chartDateLimit, DateTime earliestStartDateTime) {
        log.debug("dateGraphDataProvider Called");
        List<Object[]> values = new ArrayList<>();
        if (!data.isEmpty()) {
            DateTime datePrime = new DateTime(data.get(0).getPointDataTimeStamp());
            ReadableInstant dateLimit = new DateTime(data.get(data.size() - 1).getPointDataTimeStamp());
            datePrime = (datePrime.isAfter(chartDatePrime)) ? chartDatePrime : datePrime;                   // the earlier of chartDatePrime and this data set's datePrime
            datePrime = (datePrime.isBefore(earliestStartDateTime)) ? earliestStartDateTime : datePrime;  // but limited to the earliest day 
            dateLimit = (dateLimit.isBefore(chartDateLimit)) ? chartDateLimit : dateLimit;
            int days = Days.daysBetween(datePrime, dateLimit).getDays();
            log.debug("DatePrime: " + datePrime.toString() + " DateLimit:" + dateLimit.toString() +  " DaysBetween:" + days);
            
            List<PointValueHolder> rangeList = new ArrayList<>();
            for (PointValueHolder pvh : data) {
                DateTime itemTimeStamp = new DateTime(pvh.getPointDataTimeStamp().getTime());
                int year = datePrime.getYear();
                int monthOfYear = datePrime.getMonthOfYear();
                int dayOfMonth = datePrime.getDayOfMonth();
                itemTimeStamp = itemTimeStamp.withDate(year, monthOfYear, dayOfMonth);
                SimplePointValue updatePvh = new SimplePointValue(pvh.getId(), itemTimeStamp.toDate(), pvh.getType(), pvh.getValue());
                PointValueHolder set = updatePvh;
                rangeList.add(set);
            }
            for (int daysCtr = 0; daysCtr <= days; daysCtr++) {
                for (PointValueHolder pvh : rangeList) {
                    DateTime itemTimeStamp = new DateTime(pvh.getPointDataTimeStamp().getTime());
                    itemTimeStamp = itemTimeStamp.plusDays(daysCtr);
                    double itemPoint = pvh.getValue();
                    Object[] value = new Object[] { itemTimeStamp.getMillis(), itemPoint };
                    values.add(value);
                }
            }
        }
        log.debug("DateGraphDataProvider:Amount Returned:" + values.size());
        return values;
    }
    
    @Override
    public  List<Object[]> graphDataProvider(List<PointValueHolder> data) {
        log.debug("graphDataProvider Called");
        List<Object[]> values = new ArrayList<>();
        for (PointValueHolder pvh : data) {
            Object[] value;
            value = new Object[] { pvh.getPointDataTimeStamp().getTime(), pvh.getValue() };
            values.add(value);
        }
        log.debug("GraphDataProvider:Amount Returned:" + values.size());
        return values;
    }

    @Override
    public Instant getNextRefreshTime(Instant lastUpdateTime) {
        return lastUpdateTime.plus(MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH);
    }
    
    @Override
    public long getRefreshMilliseconds() {
        return MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH.getMillis();
    }
}

