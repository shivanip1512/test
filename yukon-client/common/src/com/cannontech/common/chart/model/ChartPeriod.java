package com.cannontech.common.chart.model;

import java.util.Date;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Enum which represents the time period over which data will be displayed in
 * the chart
 */
public enum ChartPeriod implements DisplayableEnum {
    YEAR {
        @Override
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            return ChartInterval.WEEK;
        }
    },
    THREEMONTH {
        @Override
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            return ChartInterval.DAY;
        }
    },
    MONTH {
        @Override
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            return ChartInterval.DAY;
        }
    },
    WEEK {
        @Override
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            return ChartInterval.HOUR;
        }
    },
    DAY {
        @Override
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            return ChartInterval.FIVEMINUTE;
        }
    },
    NOPERIOD {
        @Override
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            
            // choose interval based on how many days apart the two dates are
            // note: this method doesn't account for day light savings, but its quick and will be good enough to pick an interval
            long diff = Math.abs( startDate.getTime() - endDate.getTime() );
            int dayDiff = (int)Math.floor(diff/1000/60/60/24);  

            if (dayDiff >= 450) {
                return ChartInterval.MONTH;
            } else if (dayDiff >= 180) {
                return ChartInterval.WEEK;
            } else if(dayDiff >= 7) {
                return ChartInterval.DAY;
            } else if(dayDiff < 7 && dayDiff > 1) {
                return ChartInterval.HOUR;
            } else {
                return ChartInterval.FIFTEENMINUTE;
            }
        }

    };

    @Override
    public String getFormatKey() {
    	return baseKey + name();
    }

    private static String baseKey = "yukon.common.chartPeriod.";

    public abstract ChartInterval getChartUnit(Date startDate, Date endDate);
}
