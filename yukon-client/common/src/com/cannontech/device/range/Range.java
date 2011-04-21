package com.cannontech.device.range;

import java.util.LinkedList;
import java.util.List;

import com.cannontech.common.pao.PaoType;

/**
 * Models a range of values defined by one or more value intervals. Each interval is defined 
 * by a lower and upper bound. These bounds are inclusive.
 * 
 */
public abstract class Range<T extends Comparable<T>> {
    
    public class Interval<E> {
        
        private Comparable<E> lowerBound;
        private Comparable<E> upperBound;
        
        public Interval(Comparable<E> lowerBound, Comparable<E> upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }
        
        public boolean isWithinBounds (E value) {
            return lowerBound.compareTo(value) <= 0 && upperBound.compareTo(value) >= 0;
        }
        
        public String toString() {
            
            if(lowerBound.equals(upperBound)) {
                return "" + lowerBound;
            } else {
                return ""+ lowerBound + " - " + upperBound;
            }
        }
    }
    
    private PaoType paoType;
    private List<Interval<T>> intervals;
    
    public Range() {
        intervals = new LinkedList<Interval<T>>();
    }
    
    public Range(T lowerBound, T upperBound) {
        this();
        addInterval(lowerBound, upperBound);
    }
    
    public Range(String range) {
        this();
        
        if(range == null) {
            return;
        }
        String[] intervals = range.split(",");
        
        for(String interval : intervals) {
            parseAndAddInterval(interval);
        }
    }
    
    private void parseAndAddInterval(String subString) {
        String[] bounds = subString.split("-");
        
        if (bounds.length == 1) {
            T atomic = parseToken(bounds[0].trim());
            addInterval(atomic, atomic);
        } else if(bounds.length == 2) {
            T lowerBound = parseToken(bounds[0].trim());
            T upperBound = parseToken(bounds[1].trim());

            addInterval(lowerBound, upperBound);
        } else {
            throw new InvalidRangeException();
        }       
    }
    
    private void addInterval(T lowerBound, T upperBound) {
        Interval<T> interval = new Interval<T>(lowerBound, upperBound);
        intervals.add(interval);
    }
    
    protected abstract T parseToken(String token);

    /**
     * Tests if the parameters is a valid value within the defined range. 
     * In order to be valid, it has to be within at least one interval that defines the range or
     * equal to one of it's endpoints since the interval is inclusive.  
     * @param value
     * @return true if the given value is within range, otherwise false
     */
    public boolean isWithinRange(T value) {
        
        for (Interval<T> interval : intervals) {
            if (interval.isWithinBounds(value)) {
                return true;
            }
        }
        
        return false;
    }
    
    public PaoType getPaoType() {
        return paoType;
    }
    
    public String toString() {
        String str = "";
        
        for(int i = 0; i < intervals.size(); i++) {
            Interval<T> interval = intervals.get(i);
            str += interval;
            if(i < intervals.size() - 1) {
                str += ", ";
            }
        }
        
        return str;
    }
}
