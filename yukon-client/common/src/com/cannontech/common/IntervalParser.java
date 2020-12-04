package com.cannontech.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;

/**
 * Creates a list of intervals for stop and start dates, using the interval provided
 * 
15 min intervals
selected:[02/04/2020 01:27:00-02/04/2020 02:27:00] intervals created:[02/04/2020 01:30:00,02/04/2020 01:45:00,02/04/2020 02:00:00,02/04/2020 02:15:00]
selected:[02/04/2020 01:00:00-02/04/2020 02:00:00] intervals created:[02/04/2020 01:15:00,02/04/2020 01:30:00,02/04/2020 01:45:00,02/04/2020 02:00:00]
selected:[02/04/2020 01:05:00-02/04/2020 02:10:00] intervals created:[02/04/2020 01:15:00,02/04/2020 01:30:00,02/04/2020 01:45:00,02/04/2020 02:00:00]

30 min intervals
selected:[02/04/2020 01:27:00-02/04/2020 02:27:00] intervals created:[02/04/2020 01:30:00,02/04/2020 02:00:00]
selected:[02/04/2020 01:27:00-02/04/2020 01:47:00] intervals created:[02/04/2020 01:30:00]
selected:[02/04/2020 01:15:00-02/04/2020 01:30:00] intervals created:[02/04/2020 01:30:00]
selected:[02/04/2020 01:15:00-02/04/2020 01:20:00] intervals created:[]
(logged as info -  Interval MINUTES_30 is not valid for date range 02/04/2020 01:15:00-02/04/2020 01:20:00)

1 hour
 selected:[02/04/2020 01:15:00-02/04/2020 05:30:00] intervals created:[02/04/2020 02:00:00,02/04/2020 03:00:00,02/04/2020 04:00:00,02/04/2020 05:00:00]
 selected:[02/04/2020 00:00:00-02/05/2020 00:00:00] intervals created:[
 02/04/2020 01:00:00,
 02/04/2020 02:00:00,
 02/04/2020 03:00:00,
 02/04/2020 04:00:00,
 02/04/2020 05:00:00,
 02/04/2020 06:00:00,
 02/04/2020 07:00:00,
 02/04/2020 08:00:00,
 02/04/2020 09:00:00,
 02/04/2020 10:00:00,
 02/04/2020 11:00:00,
 02/04/2020 12:00:00,
 02/04/2020 13:00:00,
 02/04/2020 14:00:00,
 02/04/2020 15:00:00,
 02/04/2020 16:00:00,
 02/04/2020 17:00:00,
 02/04/2020 18:00:00,
 02/04/2020 19:00:00,
 02/04/2020 20:00:00,
 02/04/2020 21:00:00,
 02/04/2020 22:00:00,
 02/04/2020 23:00:00,
 02/05/2020 00:00:00]
 

1 day
selected:[02/01/2020 00:00:00-02/05/2020 00:00:00] intervals created:[02/02/2020 00:00:00,02/03/2020 00:00:00,02/04/2020 00:00:00,02/05/2020 00:00:00]
selected:[02/01/2020 00:15:00-02/05/2020 00:45:00] intervals created:[02/02/2020 00:00:00,02/03/2020 00:00:00,02/04/2020 00:00:00,02/05/2020 00:00:00]
*/
public class IntervalParser {
    /* example of adding new 5 min interval: TimeIntervals.MINUTES_5, ChronoUnit.MINUTES
    no other changes required */
    private static final Map<TimeIntervals, ChronoUnit> intervalDefinition = Map.of(
            TimeIntervals.HOURS_1, ChronoUnit.HOURS,
            TimeIntervals.DAYS_1, ChronoUnit.DAYS,
            TimeIntervals.MINUTES_5, ChronoUnit.MINUTES,
            TimeIntervals.MINUTES_15, ChronoUnit.MINUTES,
            TimeIntervals.MINUTES_30, ChronoUnit.MINUTES);
    private boolean hasValidInterval = true;
    private Map<Long, Date> intervals;
    private Range<Instant> range;
    private Logger log;
    private DateFormattingService dateFormattingService;

    public IntervalParser(Instant startDate, Instant stopDate, TimeIntervals interval,
            DateFormattingService dateFormattingService,
            YukonUserContext context, Logger log) {
        this.log = log;
        this.dateFormattingService = dateFormattingService;
        ChronoUnit unit = intervalDefinition.get(interval);
        if (unit == null) {
            throw new RuntimeException("Interval definition doesn't exist for: "+ interval);
        }
        
        
         //time of the first interval
        Instant firstInterval = findInterval(startDate, interval, true, unit, context);
        log.debug("First Interval {}", format(firstInterval, context));
        // time of the last interval
        Instant lastInterval = findInterval(stopDate, interval, false, unit, context);
        log.debug("Last Interval {}", format(lastInterval, context));
        range = Range.inclusive(firstInterval, lastInterval);
        if (firstInterval.isAfter(lastInterval)) {
            log.info("Interval {} is not valid for date range {}-{}", interval, format(startDate, context),
                    format(stopDate, context));
            hasValidInterval = false;
        }
        intervals = createIntervals(interval, firstInterval, lastInterval, unit);
        logIntervals(startDate, stopDate, context);
    }

    private void logIntervals(Instant startDate, Instant stopDate, YukonUserContext context) {
        if (log.isDebugEnabled()) {
            log.debug("selected:[{}-{}] intervals created:[{}]", format(startDate, context), format(stopDate, context),
                    intervals.values().stream().map(time -> format(new Instant(time.getTime()), context))
                            .collect(Collectors.joining(",")));
        } else {
            log.info("selected:[{}-{}] intervals created: {}", format(startDate, context), format(stopDate, context),
                    intervals.size());
        }
    }

    /**
     * Creates intervals starting from the firstInterval and ending with the lastInterval
     */
    private Map<Long, Date> createIntervals(TimeIntervals interval, Instant firstInterval, Instant lastInterval, ChronoUnit unit) {
        Map<Long, Date> intervals = new LinkedHashMap<>();
        Instant first = firstInterval;
        while (first.isBefore(lastInterval) || first.equals(lastInterval)) {
            intervals.put(first.toDate().getTime(), first.toDate());
            if (unit == ChronoUnit.MINUTES) {
                first = first.toDateTime().plusMinutes((int) interval.getDuration().getStandardMinutes()).toInstant();
            } else if (unit == ChronoUnit.HOURS) {
                first = first.toDateTime().plusHours((int) interval.getDuration().getStandardHours()).toInstant();
            } else if (unit == ChronoUnit.DAYS) {
                first = first.toDateTime().plusDays((int) interval.getDuration().getStandardDays()).toInstant();
            }
        }
        return intervals;
    }

    public boolean hasValidInterval() {
        return hasValidInterval;
    }

    public boolean containsInterval(Date interval) {
        return intervals.containsKey(interval.getTime());
    }

    public Date getIntervalDateForTimeLong(long intervalTime) {
        return intervals.get(intervalTime);
    }
    
    /**
     * Returns all intervals
     */
    public List<Date> getIntervals() {
        return intervals.values().stream()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Returns a date range of firstInterval and lastInterval
     */
    public Range<Instant> getRange() {
        return range;
    }

    private Instant findInterval(Instant date, TimeIntervals interval, boolean isFirstInterval, ChronoUnit unit,
            YukonUserContext context) {
        ZoneId zone = context.getTimeZone().toZoneId();
        LocalDateTime time = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(date.getMillis()), zone);

        LocalDateTime localTime = createInterval(interval, time, unit);
        if (localTime == null) {
            throw new RuntimeException("Unable to calculate interval for " + format(date, context) + " interval:" + interval
                    + " isFirstInterval:" + isFirstInterval);
        }

        if (isFirstInterval) {
            localTime = localTime.plus(interval.getSeconds(), ChronoUnit.SECONDS);
        }
        ZonedDateTime zonedDateTime = localTime.atZone(zone);
        return new Instant(zonedDateTime.toInstant().toEpochMilli());
    }

    /**
     * Returns an interval
     */
    private LocalDateTime createInterval(TimeIntervals interval, LocalDateTime time, ChronoUnit unit) {
        LocalDateTime localTime = null;
        if (unit == ChronoUnit.MINUTES) {
            long minutes = TimeUnit.SECONDS.toMinutes(interval.getSeconds());
            localTime = time.truncatedTo(ChronoUnit.HOURS).plusMinutes(minutes * (time.getMinute() / minutes));
        } else if (unit == ChronoUnit.HOURS) {
            long hours = TimeUnit.SECONDS.toHours(interval.getSeconds());
            localTime = time.truncatedTo(ChronoUnit.DAYS).plusHours(hours * (time.getHour() / hours));
        } else if (unit == ChronoUnit.DAYS) {
            localTime = time.truncatedTo(ChronoUnit.DAYS);
        }
        return localTime;
    }
    
    private String format(Instant date, YukonUserContext context) {
        return dateFormattingService.format(date, DateFormatEnum.DATEHM, context);
    }
}