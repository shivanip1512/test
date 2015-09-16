/**
 * Class TrendDataServiceImpl
 * <p>
 * TrendDataServiceImp provides consumable data factories to be used for Graph Data
 * <p>
 * 
 * @author      Thomas Red-Cloud
 * @email       ThomasRedCloud@Eaton.com
 * @version     %I%, %G%
 * @since       1.0
 */
package com.cannontech.web.tools.trends.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
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
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.trends.TrendDataController;
import com.cannontech.web.tools.trends.service.TrendDataService;
@Service
public class TrendDataServiceImpl implements TrendDataService {

    @Autowired private RawPointHistoryDao rawPointHistoryDao;

    private static final Logger log = YukonLogManager.getLogger(TrendDataController.class);

   @Override 
   public List<PointValueHolder> rawPointHistoryDataProvider(int pointId) {
        Instant end = Instant.now();
        Instant start = end.minus(Duration.standardDays(365 * 2));
        Range<Instant> instantRange = Range.inclusive(start, end);
        return  rawPointHistoryDao.getPointData(pointId, instantRange, Order.FORWARD);
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
        for (PointValueHolder pvh : data) {
            Object[] value;
            DateTime timestamp = new DateTime(pvh.getPointDataTimeStamp().getTime());
            timestamp = timestamp.plusDays(1);
            value = new Object[] { timestamp.getMillis(), pvh.getValue() };
            values.add(value);
        }
        log.debug("YesterdayGraphDataProvider:Amount Returned:" + values.size());
        return values;
    }
    

    @Override
    public  List<Object[]> usageGraphDataProvider(List<PointValueHolder> data) {
        log.debug("UsageGraphDataProvider Called");
        List<Object[]> values = new ArrayList<>();
        DateTime dateNow = new DateTime();
        DateTime datePrime = dateNow.minusYears(2);
        DateTime currentTimeStamp = new DateTime();
        double currentPoint = 0;
        double previousPoint = 0;

        for (PointValueHolder pvh : data) {
            DateTime itemTimeStamp = new DateTime(pvh.getPointDataTimeStamp().getTime());
            DateTime compareTimeStamp = itemTimeStamp.withTimeAtStartOfDay();
            boolean flagNow = itemTimeStamp.isBefore(dateNow);
            boolean flagPrime = itemTimeStamp.isAfter(datePrime);
            if (flagPrime && flagNow) {
                if (values.isEmpty() || compareTimeStamp.isAfter(currentTimeStamp)) {
                    currentTimeStamp = compareTimeStamp;
                    currentPoint = pvh.getValue();
                    double mPoint = currentPoint - previousPoint;
                    Object[] value = new Object[] { itemTimeStamp.getMillis(), mPoint };
                    previousPoint = currentPoint;
                    values.add(value);
                }
            } else {
                continue;
            }
        }
        log.debug("UsageGraphDataProvider:Amount Returned:" + values.size());
        return values;
    }

    @Override
    public List<Object[]> dateGraphDataProvider(List<PointValueHolder> data, DateTime chartDatePrime, ReadableInstant chartDateLimit) {
        log.debug("dateGraphDataProvider Called");
        List<Object[]> values = new ArrayList<>();
        if (!data.isEmpty()) {
            DateTime datePrime = new DateTime(data.get(0).getPointDataTimeStamp());
            ReadableInstant dateLimit = new DateTime(data.get(data.size() - 1).getPointDataTimeStamp());
            datePrime = (datePrime.isAfter(chartDatePrime)) ? chartDatePrime : datePrime;
            dateLimit = (dateLimit.isBefore(chartDateLimit)) ? chartDateLimit : dateLimit;
            int days = Days.daysBetween(datePrime, dateLimit).getDays();
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

}

