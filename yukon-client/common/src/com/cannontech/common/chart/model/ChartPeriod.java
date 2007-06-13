package com.cannontech.common.chart.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Enum which represents the time period over which data will be displayed in
 * the chart
 */
public enum ChartPeriod {
    YEAR("Year") {
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
    THREEMONTH("Three Month") {
        public Date getStartDate(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.MONTH, -3);

            return new Date(cal.getTimeInMillis());
        }

        public ChartInterval getChartUnit() {
            return ChartInterval.DAY;
        }
    },
    MONTH("Month") {
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
    WEEK("Week") {
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
    DAY("Day") {
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

    private String label = null;

    private ChartPeriod(String label) {
        this.label = label;
    }

    /**
     * Method to get the start date for this period based on the date passed in
     * @param date - End date of period
     * @return Date minus length of period
     */
    public abstract Date getStartDate(Date date);

    public abstract ChartInterval getChartUnit();

    public String getPeriodLabel() {
        return label;
    }
}
