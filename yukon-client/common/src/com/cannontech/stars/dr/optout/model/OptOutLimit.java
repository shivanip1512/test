package com.cannontech.stars.dr.optout.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.springframework.core.style.ToStringCreator;

/**
 * Model object to represent an opt out limit
 */
public class OptOutLimit {

	private int limit = 0;
	private Integer startMonth;
	private Integer stopMonth;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * Starting month which this opt out limit applies (Limit applies to the first day of the
	 * month and forward)
	 * @return Month (1-12)
	 */
	public Integer getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(Integer startMonth) {
		this.startMonth = startMonth;
	}

	/**
	 * Month through which this opt out limit applies (Limit applies through the last day of
	 * the month)
	 * @return Month (1-12)
	 */
	public Integer getStopMonth() {
		return stopMonth;
	}

	public void setStopMonth(Integer stopMonth) {
		this.stopMonth = stopMonth;
	}
	
	/**
     * Returns true if the current month is under the OptOutLimit, taking
     * year-crossovers into account. Month values for startMonth, stopMonth,
     * currentMonth use 1-12 for Jan-Dec. Please see examples below.
     * 
     * <pre>
     * For Limit (startMonth=5, stopMonth=9), isMonthUnderLimit(4) would return false
     * For Limit (startMonth=5, stopMonth=9), isMonthUnderLimit(5) would return true
     * For Limit (startMonth=5, stopMonth=9), isMonthUnderLimit(6) would return true
     * For Limit (startMonth=10, stopMonth=4), isMonthUnderLimit(11) would return true
     * For Limit (startMonth=10, stopMonth=4), isMonthUnderLimit(2) would return true
     * For Limit (startMonth=10, stopMonth=4), isMonthUnderLimit(5) would return false 
     * </pre>
     * @param currentMonth
     * @return true, if currentMonth under OptOutLimit
     */
    public boolean isReleventMonth(int currentMonth) {
        boolean monthUnderLimit = false;
        if (startMonth > stopMonth) {
            if ((currentMonth >= startMonth && currentMonth <= 12) || (currentMonth >= 1 && currentMonth <= stopMonth)) {
                monthUnderLimit = true;
            }
        } else if (currentMonth >= startMonth && currentMonth <= stopMonth) {
            monthUnderLimit = true;
        }

        return monthUnderLimit;
    }
    
    /**
     * Returns the startDate of the OptOutLimit, taking
     * year-crossovers into account. Month values for startMonth, stopMonth,
     * currentMonth use 1-12 for Jan-Dec. Please see examples below.  Examples assume
     * current year is 2009.
     * 
     * <pre>
     * For Limit (startMonth=5, stopMonth=9), getOptOutLimitStartDate(4) would throw IllegalArgumentException
     * For Limit (startMonth=5, stopMonth=9), getOptOutLimitStartDate(5) would return 05/01/2009
     * For Limit (startMonth=5, stopMonth=9), getOptOutLimitStartDate(6) would return 05/01/2009
     * For Limit (startMonth=10, stopMonth=4), getOptOutLimitStartDate(2) would return 10/01/2008
     * For Limit (startMonth=10, stopMonth=4), getOptOutLimitStartDate(11) would return 10/01/2009 
     * For Limit (startMonth=10, stopMonth=4), getOptOutLimitStartDate(5) would throw IllegalArgumentException 
     * </pre>
     * @param currentMonth
     * @return startDate of the OptOutLimit
     * @throws IllegalArgumentException if currentMonth not under this OptOutLimit
     */
    public Instant getOptOutLimitStartDate(DateTimeZone energyCompanyTimeZone) {
        DateTime dateTime = new DateTime(energyCompanyTimeZone);
        int currentMonth = dateTime.getMonthOfYear();
        
        if (!isReleventMonth(currentMonth)) {
            throw new IllegalArgumentException("currentMonth=[" + currentMonth + "], is not under this OptOutLimit [" + toString() + "]");
        }

        // Get the first day of the start month of the limit at midnight
        DateTime startDateTime = new DateTime(energyCompanyTimeZone);
        startDateTime = startDateTime.withMonthOfYear(startMonth);
        startDateTime = startDateTime.withDayOfMonth(1);
        startDateTime = startDateTime.withTime(0, 0, 0, 0);

        // See if the OptOutLimit started in the current or last year
        if (startMonth > stopMonth) {
            if (currentMonth <= stopMonth) {
                // OptOutLimit started last year
                startDateTime = startDateTime.minusYears(1);
            }
        }
        
        return startDateTime.toInstant();
    }
    
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("startMonth", getStartMonth());
        tsc.append("stopMonth", getStopMonth());
        tsc.append("limit", getLimit());

        return tsc.toString();
    }
}
