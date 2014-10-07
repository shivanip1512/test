package com.cannontech.common.chart.model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.Range;

/**
 * Enum which represents the time period over which data will be displayed in
 * the chart
 */
public enum ChartPeriod implements DisplayableEnum {
    YEAR(Duration.standardDays(365)) {
        @Override
        public ChartInterval getChartUnit(Range<Date> dateRange) {
            return ChartInterval.WEEK;
        }
    },
    THREEMONTH(Duration.standardDays(90)) {
        @Override
        public ChartInterval getChartUnit(Range<Date> dateRange) {
            return ChartInterval.DAY;
        }
    },
    MONTH(Duration.standardDays(30)) {
        @Override
        public ChartInterval getChartUnit(Range<Date> dateRange) {
            return ChartInterval.DAY;
        }
    },
    WEEK(Duration.standardDays(7)) {
        @Override
        public ChartInterval getChartUnit(Range<Date> dateRange) {
            return ChartInterval.HOUR;
        }
    },
    DAY(Duration.standardDays(1)) {
        @Override
        public ChartInterval getChartUnit(Range<Date> dateRange) {
            return ChartInterval.FIVEMINUTE;
        }
    },
    NOPERIOD(Duration.ZERO) {
        @Override
        public ChartInterval getChartUnit(Range<Date> dateRange) {
            // choose interval based on how many days apart the two dates are
            // note: this method doesn't account for day light savings, but its quick and will be good enough to pick an interval
            long millisRange = Math.abs(dateRange.getMin().getTime() - dateRange.getMax().getTime());
            int dayDiff = (int) TimeUnit.MILLISECONDS.toDays(millisRange);

            if (dayDiff >= 450) {
                return ChartInterval.MONTH;
            } else if (dayDiff >= 180) {
                return ChartInterval.WEEK;
            } else if(dayDiff >= 30) {
                return ChartInterval.DAY;
            } else if(dayDiff < 30 && dayDiff > 1) {
                return ChartInterval.HOUR;
            } else {
                return ChartInterval.FIFTEENMINUTE;
            }
        }

    };

    private final Duration duration;

    private ChartPeriod(Duration duration) {
        this.duration = duration;
    }

    public Date backdate(Date date) {
        return new Instant(date).minus(duration).toDate();
    }

    /**
     * Returns the matching enum or null if no enum maches the string
     */
    public static ChartPeriod fromString(String chartIntervalStr) {
        if (StringUtils.isBlank(chartIntervalStr)) {
            return null;
        }
        try {
            return ChartPeriod.valueOf(chartIntervalStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Override
    public String getFormatKey() {
    	return baseKey + name();
    }

    private static String baseKey = "yukon.common.chartPeriod.";

    public abstract ChartInterval getChartUnit(Range<Date> dateRange);
}
