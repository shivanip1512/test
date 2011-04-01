package com.cannontech.device.range.v2;

import java.util.LinkedList;
import java.util.List;

import com.cannontech.common.pao.PaoType;

public class Range<T extends Comparable<T>> {
    
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
            return ""+ lowerBound + " - " + upperBound; 
        }
    }
    
    private PaoType paoType;
    private String rangeDescription = "";
    private List<Interval<T>> intervals;
    
    public Range() {
        intervals = new LinkedList<Interval<T>>();
    }
    
    public Range(T lowerBound, T upperBound) {
        this();
        addInterval(lowerBound, upperBound);
    }
    
    public void addInterval(T lowerBound, T upperBound) {
        Interval<T> interval = new Interval<T>(lowerBound, upperBound);
        intervals.add(interval);
    }

    
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

    public String getRangeDescription() {
        return rangeDescription;
    }

    public void setRangeDescription(String rangeDescription) {
        this.rangeDescription = rangeDescription;
    }

    public List<Interval<T>> getAddressIntervals() {
        return intervals;
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
