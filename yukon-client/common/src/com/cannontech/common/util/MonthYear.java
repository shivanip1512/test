package com.cannontech.common.util;

import org.joda.time.DateTime;

/**
 * Represents a specific month, as specified by its year and month index. (This is a 1-based index, so January = 1 and
 * December = 12).
 */
public final class MonthYear implements Comparable<MonthYear> {
    private final int monthIndex;
    private final int yearIndex;
    
    public MonthYear(int monthIndex, int yearIndex) {
        validateInputMonth(monthIndex);
        this.monthIndex = monthIndex;
        this.yearIndex = yearIndex;
    }
    
    public MonthYear(DateTime dateTime) {
        this.monthIndex = dateTime.getMonthOfYear();
        this.yearIndex = dateTime.getYear();
    }
    
    public static MonthYear now() {
        return new MonthYear(new DateTime());
    }
    
    public MonthYear plus(int months, int years) {
        validateForCalculation(months, years);
        return new MonthYear(monthIndex + months, yearIndex + years);
    }
    
    public MonthYear minus(int months, int years) {
        validateForCalculation(months, years);
        return new MonthYear(monthIndex - months, yearIndex - years);
    }
    
    public int getMonth() {
        return monthIndex;
    }
    
    public int getYear() {
        return yearIndex;
    }
    
    @Override
    public int compareTo(MonthYear other) {
        if(other.yearIndex != yearIndex) {
            return new Integer(yearIndex).compareTo(other.yearIndex);
        }
        return new Integer(monthIndex).compareTo(other.monthIndex);
    }
    
    /**
     * Eclipse generated.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + monthIndex;
        result = prime * result + yearIndex;
        return result;
    }
    
    /**
     * Eclipse generated.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MonthYear other = (MonthYear) obj;
        if (monthIndex != other.monthIndex)
            return false;
        if (yearIndex != other.yearIndex)
            return false;
        return true;
    }
    
    /**
     * Checks that the month value is between 1 and 12 inclusive.
     */
    private void validateInputMonth(int months) {
        if(months < 1 || months > 12) {
            throw new IllegalArgumentException("Invalid months: " + months);
        }
    }
    
    /**
     * Checks that months and years used for calculating a new MonthYear are within appropriate ranges.
     * Cannot add/subtract more than 11 months (switch to years) or negative months.
     * Cannot add/subtract negative years.
     */
    private void validateForCalculation(int months, int years) {
        if(months < 0 || months > 11) {
            throw new IllegalArgumentException("Invalid months: " + months);
        }
        if(years < 0) {
            throw new IllegalArgumentException("Years cannot be less than 0");
        }
    }
}
