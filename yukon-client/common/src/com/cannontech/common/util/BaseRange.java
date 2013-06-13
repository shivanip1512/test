package com.cannontech.common.util;

import com.google.common.base.Function;

public abstract class BaseRange<T extends Comparable<? super T>> implements ReadableRange<T> {
    protected T min = null;
    protected boolean includesMinValue = true;
    protected T max = null;
    protected boolean includesMaxValue = true;
   
    protected BaseRange() {
    }   
    
    protected BaseRange(T min, T max) {
        this.min = min;
        this.max = max;
    }
    
    protected BaseRange(T min, boolean includesMinValue, T max, boolean includesMaxValue) {
        this(min, max);
        this.includesMinValue = includesMinValue;
        this.includesMaxValue = includesMaxValue;
    }
    
    public T getMin() {
        return min;
    }
    
    public T getMax() {
        return max;
    }
    
    public boolean isIncludesMinValue() {
        return includesMinValue;
    }

    
    public boolean isIncludesMaxValue() {
        return includesMaxValue;
    }
    
    public final Range<T> getImmutableRange() {
        if (includesMinValue && includesMaxValue) { //completely inclusive
            return Range.inclusive(min, max);
        } else if (includesMinValue) { //one or the other is included
            return Range.inclusiveExclusive(min, max);
        } else if (includesMaxValue) {
            return Range.exclusiveInclusive(min, max);
        } else { //neither are included
            return Range.exclusive(min, max);
        }
    }
    
    public boolean intersects(T value) {
        if (value == null) {//no match
            return false;
        }
        
        if (min == null && max == null) {
            // completely unbounded, everything is included
            return true;
        }

        if (min == null) {
            int compareValue = value.compareTo(max);
            return compareValue < 0 || compareValue == 0 && includesMaxValue;
        }

        if (max == null) {
            int compareValue = value.compareTo(min);
            return compareValue > 0 || compareValue == 0 && includesMinValue;
        }

        int compareToMin = value.compareTo(min);
        if (compareToMin < 0 || compareToMin == 0 && !includesMinValue) {
            return false;
        }

        // we now know it's >/>= min
        int compareToMax = value.compareTo(max);

        return compareToMax < 0 || compareToMax == 0 && includesMaxValue;
    }

    public boolean isUnbounded() {
        return min == null && max == null;
    }

    public boolean isEmpty() {
        if (!isValid()) {
            throw new IllegalStateException();
        }

        if (min != null && min.equals(max)) {
            return !(includesMinValue && includesMaxValue);
        }

        return false;
    }

    public boolean isValid() {
        if (min == null || max == null) {
            return true;
        }
        return min.compareTo(max) <= 0;
    }
    
    /**
     * Create a new Range given the function to translate the min and max.  This is useful (along with
     * {@link CtiUtilities.DATE_FROM_INSTANT} or {@link CtiUtilities.INSTANT_FROM_DATE} for translating
     * from a Range&lt;Instant&gt; to or from a Range&lt;Date&gt;.
     */
    public <U extends Comparable<? super U>> Range<U> translate(Function<T, U> translator) {
        return new Range<U>(translator.apply(min), includesMinValue, translator.apply(max), includesMaxValue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (includesMaxValue ? 1231 : 1237);
        result = prime * result + (includesMinValue ? 1231 : 1237);
        result = prime * result + ((max == null) ? 0 : max.hashCode());
        result = prime * result + ((min == null) ? 0 : min.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseRange<?> other = (BaseRange<?>) obj;

        if (max == null) {
            if (other.max != null)
                return false;
        } else if (!max.equals(other.max))
            return false;
        if (min == null) {
            if (other.min != null)
                return false;
        } else if (!min.equals(other.min))
            return false;

        // If max is null, it doesn't matter if the includesMaxValues don't
        // match since they're both unbounded at the top.
        if (max != null && includesMaxValue != other.includesMaxValue)
            return false;
        if (min != null && includesMinValue != other.includesMinValue)
            return false;

        return true;
    }
    
    
}
