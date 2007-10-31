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

        public ChartInterval getChartUnit(Date...dates) {
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

        public ChartInterval getChartUnit(Date...dates) {
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

        public ChartInterval getChartUnit(Date...dates) {
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

        public ChartInterval getChartUnit(Date...date) {
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

        public ChartInterval getChartUnit(Date...date) {
            return ChartInterval.FIVEMINUTE;
        } 
    },
    NOPERIOD("NoPeriod") {
        public Date getStartDate(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            return new Date(cal.getTimeInMillis());

        }

        public ChartInterval getChartUnit(Date...dates) {
            
            Date startDate = dates[0];
            Date endDate = dates[1];
            
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

    /**
     * Method to get the start date for this period based on the date passed in
     * @param date - End date of period
     * @return Date minus length of period
     */
    public abstract Date getStartDate(Date date);

    public abstract ChartInterval getChartUnit(Date...dates);

    public String getPeriodLabel() {
        return label;
    }
}
