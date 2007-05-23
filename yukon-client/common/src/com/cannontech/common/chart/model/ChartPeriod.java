package com.cannontech.common.chart.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public enum ChartPeriod {
    YEAR {
        public Date getStartDate(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.YEAR, -1);

            return new Date(cal.getTimeInMillis());
        }

        public ChartInterval getChartUnit() {
            return ChartInterval.DAY;
        }
    },
    MONTH {
        public Date getStartDate(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.MONTH, -1);

            return new Date(cal.getTimeInMillis());
        }

        public ChartInterval getChartUnit() {
            return ChartInterval.DAY;
        }
    },
    WEEK {
        public Date getStartDate(Date date) {
            
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            
            return new Date(cal.getTimeInMillis());
        }
        
        public ChartInterval getChartUnit() {
            return ChartInterval.HOUR;
        }
    },
    DAY {
        public Date getStartDate(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.DATE, -1);

            return new Date(cal.getTimeInMillis());

        }
        public ChartInterval getChartUnit() {
            return ChartInterval.MINUTE;
        }
    };

    public abstract Date getStartDate(Date date);

    public abstract ChartInterval getChartUnit();
}
