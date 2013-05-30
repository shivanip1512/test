package com.cannontech.common.util;

import com.google.common.base.Function;

public class Range<T extends Comparable<? super T>> {
    private final T min;
    private final boolean includesMinValue;
    private final T max;
    private final boolean includesMaxValue;

    /**
     * Create a range
     * max & min        Default to null
     * includesMinValue Default to exclusive
     * includesMaxvalue Default to exclusive
     */
    public Range() {
        this.min = null;
        this.includesMinValue = false;
        this.max = null;
        this.includesMaxValue = false;
    }
    
    /** 
     * @param min   The minimum value. A null value represents no minimum.
     * @param max   The maximum value. A null value represents no maximum.
     * includesMinValue Inclusive if min value specified, otherwise exclusive.
     * includesMaxValue Inclusive if max value specified, otherwise exclusive.
     * (inclusive - true, exclusive - false)
     */
    public Range(T min, T max) {
        this.min = min;
        this.max = max;
        
        if (min != null) {
            //min specified -> inclusive
            this.includesMinValue = true;
        } else {
            //exclusive
            this.includesMinValue = false;
        }

        if (max != null) {
            //max specified -> inclusive
            this.includesMaxValue = true;
        } else {
            //exclusive
            this.includesMaxValue = false;
        }
    }
    
    /**
     * Create a range from min to max.
     * @param min The minimum value. A null value represents no minimum.
     * @param max The maximum value. A null value represents no maximum.
     * @throws IllegalArgumentException if min and max are specified and
     * max less than min
     */
    public Range(T min, boolean includesMinValue, T max, boolean includesMaxValue) {
        this.min = min;
        this.max = max;
        this.includesMinValue = includesMinValue;
        this.includesMaxValue = includesMaxValue;
    }

    public static <U extends Comparable<? super U>> Range<U> fromExclusive(U min) {
        return new Range<U>(min, false, null, true);
    }

    public T getMin() {
        return min;
    }

    public boolean isIncludesMinValue() {
        return includesMinValue;
    }

    public T getMax() {
        return max;
    }

    public boolean isIncludesMaxValue() {
        return includesMaxValue;
    }
    
    public boolean intersects(T value) {
        if (min == null && max == null) {
            // completely unbounded, everything is included
            return true;
        }
        if (value == null) {//no match
            return false;
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

	/**
	 * Creates a Range where the min and max values are both included.
	 */
    public static <U extends Comparable<? super U>> Range<U> inclusive(U min, U max) {
        return new Range<U>(min, true, max, true);
    }

    /**
     * Creates a Range where the min and max values are both not included.
     */
    public static <U extends Comparable<? super U>> Range<U> exclusive(U min, U max) {
        return new Range<U>(min, false, max, false);
    }

    /**
     * Creates a Range where the min value is included, but the max value is not.
     */
    public static <U extends Comparable<? super U>> Range<U> inclusiveExclusive(U min, U max) {
        return new Range<U>(min, true, max, false);
    }

    /**
     * Creates a Range where the max value is included, but the min value is not.
     */
    public static <U extends Comparable<? super U>> Range<U> exclusiveInclusive(U min, U max) {
        return new Range<U>(min, false, max, true);
    }

	@Override
    public String toString() {
        return min + " " + (includesMinValue ? "inclusive" : "exclusive")
            + " to " + max + " " + (includesMaxValue ? "inclusive" : "exclusive");
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
        Range<?> other = (Range<?>) obj;

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
