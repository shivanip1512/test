package com.cannontech.common.util;



public final class Range<T extends Comparable<? super T>> extends BaseRange<T> implements ReadableRange<T> {

    // We can use any class for the range type here since both will be null anyway.
    private final static Range<?> unbounded = new Range<Boolean>(null, true, null, true);
    
    /**
     * It is recommended to use the factory methods instead of this constructor.
     * This is public to accommodate special circumstances.
     * @param min The minimum value. Null represents no value.
     * @param includesMinValue True if min included, false if excluded.
     * @param max The maximum value. Null represents no value.
     * @param includesMaxValue True if max included, false if excluded.
     */
    public Range(T min, boolean includesMinValue, T max, boolean includesMaxValue) {
        this.min = min;
        this.max = max;
        this.includesMinValue = includesMinValue;
        this.includesMaxValue = includesMaxValue;
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> Range<T> unbounded() {
        return (Range<T>) unbounded;
    }
    
    public static <U extends Comparable<? super U>> Range<U> fromInclusive(U min) {
        return new Range<U>(min, true, null, true);
    }

    public static <U extends Comparable<? super U>> Range<U> fromExclusive(U min) {
        return new Range<U>(min, false, null, true);
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
    
}

