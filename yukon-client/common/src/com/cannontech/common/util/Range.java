package com.cannontech.common.util;

public class Range<T extends Comparable<T>> {
    private T min = null;
    private boolean includesMinValue = true;
    private T max = null;
    private boolean includesMaxValue = true;

    /**
     * Create an unbounded range.
     */
    public Range() {
    }

    /**
     * Create a range from min to max.
     * @param min The minimum value. A null value represents no minimum.
     * @param max The maximum value. A null value represents no maximum.
     * @throws IllegalArgumentException if min and max are specified and
     * max less than min
     */
    public Range(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public Range(T min, boolean includesMinValue, T max, boolean includesMaxValue) {
        this(min, max);
        this.includesMinValue = includesMinValue;
        this.includesMaxValue = includesMaxValue;
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public boolean isIncludesMinValue() {
        return includesMinValue;
    }

    public void setIncludesMinValue(boolean includesMinValue) {
        this.includesMinValue = includesMinValue;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public boolean isIncludesMaxValue() {
        return includesMaxValue;
    }

    public void setIncludesMaxValue(boolean includesMaxValue) {
        this.includesMaxValue = includesMaxValue;
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
		if (min == null || max == null) {
			return false;
		}

		if (min.equals(max)) {
			return !(includesMinValue && includesMaxValue);
		}

		return min.compareTo(max) > 0;
	}

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
