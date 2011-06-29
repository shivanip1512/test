package com.cannontech.common.chart.model;

import java.util.Date;

/**
 * Enum which represents the time period over which data will be displayed in
 * the chart
 */
public enum ChartPeriod {
    YEAR("Year") {
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            return ChartInterval.WEEK;
        }
    },
    THREEMONTH("Three Month") {
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            return ChartInterval.DAY;
        }
    },
    MONTH("Month") {
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            return ChartInterval.DAY;
        }
    },
    WEEK("Week") {
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            return ChartInterval.HOUR;
        }
    },
    DAY("Day") {
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            return ChartInterval.FIVEMINUTE;
        } 
    },
    NOPERIOD("NoPeriod") {
        public ChartInterval getChartUnit(Date startDate, Date endDate) {
            
            // choose interval based on how many days apart the two dates are
            // note: this method doesn't account for day light savings, but its quick and will be good enough to pick an interval
            long diff = Math.abs( startDate.getTime() - endDate.getTime() );
            int dayDiff = (int)Math.floor(diff/1000/60/60/24);  

            // week and day are smaller intervals, everything else uses a day interval
            if(dayDiff > 7){
                return ChartInterval.DAY;
            }
            else if(dayDiff <=7 && dayDiff > 1){
                return ChartInterval.HOUR;
            }
            else{
                return ChartInterval.MINUTE;
            }
            
        } 
    };

    private String label = null;

    private ChartPeriod(String label) {
        this.label = label;
    }

    public abstract ChartInterval getChartUnit(Date startDate, Date endDate);

    public String getPeriodLabel() {
        return label;
    }
}
