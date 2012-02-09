package com.cannontech.common.chart.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * Enum of which represents the x-axis time interval between tick marks on the
 * chart
 */
public enum ChartInterval {

    MONTH {
        public void increment(Calendar cal) {
            cal.add(Calendar.MONTH, 1);
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
        public void increment(Calendar cal) {
            cal.add(Calendar.DATE, 1);
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
    DAY_MIDNIGHT {
        public void increment(Calendar cal) {
            cal.add(Calendar.DATE, 1);
        }

        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            
            int unitsAfterMidnight = 
                cal.get(Calendar.HOUR_OF_DAY) +
                cal.get(Calendar.MINUTE) +
                cal.get(Calendar.SECOND) +
                cal.get(Calendar.MILLISECOND);
            
            if (unitsAfterMidnight == 0) {
                cal.add(Calendar.DATE, -1);
                cal.set(Calendar.MILLISECOND, 1);
            } else {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 1);
            }

            return cal.getTime();
        }
    },
    HOUR {
        public Date increment(Date date) {
            long time = date.getTime();
            time += (60 * 60 * 1000);

            return new Date(time);
        }
        
        @Override
        public void increment(Calendar cal) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
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
            long time = date.getTime();
            time += (60 * 1000);

            return new Date(time);
         }

        public void increment(Calendar cal) {
            
            cal.add(Calendar.MINUTE, 1);
        }
        
        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            return new Date(cal.getTimeInMillis());
        }
        
        @Override
        public DateFormatEnum getFormat() {
            return DateFormatEnum.DATEHM;
        }

    },
    FIFTEENSECOND {
        public Date increment(Date date) {
            long time = date.getTime();
            time += (15 * 1000);

            return new Date(time);
        }

        public void increment(Calendar cal) {
            cal.add(Calendar.SECOND, 15);
        }
        
        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            cal.set(Calendar.MILLISECOND, 0);
            int i = cal.get(Calendar.SECOND);
            cal.set(Calendar.SECOND, i - (i % 15));

            return cal.getTime();
        }
        
        @Override
        public DateFormatEnum getFormat() {
            return DateFormatEnum.BOTH;
        }

    }, 
    WEEK {
        public void increment(Calendar cal) {
            cal.add(Calendar.DATE, 7);
        }

        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            return cal.getTime();
        }

    }, 
    
    FIVEMINUTE {
        public Date increment(Date date) {
            long time = date.getTime();
            time += (5 * 60 * 1000);

            return new Date(time);
        }
        
        public void increment(Calendar cal) {
            cal.add(Calendar.MINUTE, 5);
        }

        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            int i = cal.get(Calendar.MINUTE);
            cal.set(Calendar.MINUTE, i - (i % 5));

            return cal.getTime();
        }
        
        @Override
        public DateFormatEnum getFormat() {
            return DateFormatEnum.DATEHM;
        }

    },
    
    FIFTEENMINUTE {
        public Date increment(Date date) {
            long time = date.getTime();
            time += (15 * 60 * 1000);

            return new Date(time);
        }
        
        public void increment(Calendar cal) {
            cal.add(Calendar.MINUTE, 15);
        }

        public Date roundDownToIntervalUnit(Date date) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            int i = cal.get(Calendar.MINUTE);
            cal.set(Calendar.MINUTE, i - (i % 15));

            return cal.getTime();
        }
        
        @Override
        public DateFormatEnum getFormat() {
            return DateFormatEnum.DATEHM;
        }

    };

    /**
     * Method to add the given chart interval to the date
     * @param date - Date to increment
     * @return A new date whose time is the old date plus the interval
     */
    public Date increment(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        increment(cal);
        Date time = cal.getTime();
        return time;
    }
    
    /**
     * Method to add the given chart interval to the date.
     * (Should be faster than passing in a date.)
     * @param date
     * @return
     */
    public abstract void increment(Calendar date);

    /**
     * Method to round a date down to the interval unit. If the interval is DAY -
     * the milliseconds, seconds and minute will be zeroed out.
     * @param date - Date to round
     * @return The date rounded down to the interval
     */
    public abstract Date roundDownToIntervalUnit(Date date);

    /**
     * Method to get the date formatter for this interval
     * @return Date formatter
     */
    public DateFormatEnum getFormat() {
        return DateFormatEnum.DATE;
    }
    
    /**
     * Method to return the interval string for display.
     * This does NOT return a MessageSourceResolvable the enum itself.
     *
     * This will return "interval" for anything less than Hour. It's kind of a hack for the water delta converter to use 
     * since this is the interval of the chart, and not the actual interval of the data. 
     * For chart intervals >= hour, we are assuming we have data intervals that are at least an hour, too.
     * In the future, it would be better to figure out how to tie these values to ChartPeriod? or the actual meter's
     *  data recording interval. SN YUK-10416  
     */
    public MessageSourceResolvable getIntervalString() {
        return YukonMessageSourceResolvable.createDefault("yukon.common.chart.interval." + name(), name());
    }

}
