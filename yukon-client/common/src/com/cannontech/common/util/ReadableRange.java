package com.cannontech.common.util;

import com.google.common.base.Function;

public interface ReadableRange<T extends Comparable<? super T>> {
    
    T getMin();
    
    T getMax();
    
    boolean isIncludesMinValue();
    
    boolean isIncludesMaxValue();
    
    Range<T> getImmutableRange();

    boolean intersects(T value);
    
    boolean isUnbounded();
    
    boolean isEmpty();
    
    boolean isValid();
      
    /**
     * Create a new Range given the function to translate the min and max.  This is useful (along with
     * {@link CtiUtilities.DATE_FROM_INSTANT} or {@link CtiUtilities.INSTANT_FROM_DATE} for translating
     * from a Range&lt;Instant&gt; to or from a Range&lt;Date&gt;.
     */
    <U extends Comparable<? super U>> Range<U> translate(Function<T, U> translator);
}
