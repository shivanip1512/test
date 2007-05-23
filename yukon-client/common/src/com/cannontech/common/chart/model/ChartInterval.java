package com.cannontech.common.chart.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Enum of chart time intervals
 */
public enum ChartInterval {

    MONTH {
        public Date increment(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.MONTH, 1);

            return new Date(cal.getTimeInMillis());
        }

        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            return new Date(cal.getTimeInMillis());
        }

    },
    DAY {
        public Date increment(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.DATE, 1);

            return new Date(cal.getTimeInMillis());

        }

        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            return new Date(cal.getTimeInMillis());
        }

    },
    HOUR {
        public Date increment(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.HOUR_OF_DAY, 1);

            return new Date(cal.getTimeInMillis());

        }

        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            return new Date(cal.getTimeInMillis());
        }

    },
    MINUTE {
        public Date increment(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.MINUTE, 1);

            return new Date(cal.getTimeInMillis());

        }

        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            return new Date(cal.getTimeInMillis());
        }

    },
    FIFTEENSECOND {
        public Date increment(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.SECOND, 15);

            return new Date(cal.getTimeInMillis());

        }

        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            cal.set(Calendar.MILLISECOND, 0);

            return new Date(cal.getTimeInMillis());
        }

    };

    /**
     * Method to add the given chart interval to the date
     * @param date - Date to increment
     * @return A new date whose time is the old date plus the interval
     */
    public abstract Date increment(Date date);

    /**
     * Method to round a date down to the interval unit. If the interval is DAY -
     * the milliseconds, seconds and minute will be zeroed out.
     * @param date - Date to round
     * @return The date rounded down to the interval
     */
    public abstract Date roundDownToIntervalUnit(Date date);

    /**
     * Method to format a date based on the intervale
     * @param date - Date to format
     * @return Formatted date
     */
    public String format(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        return format.format(date);
    }
}
