package com.cannontech.common.util;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.PeekingIterator;


/**
 * This class is meant to be an expanded version of the Joda interval class that allows for open ended 
 * representations for start and end dates.  One use case for this class would be program enrollments.
 * In the case of an active enrollment, we would have a start date but no stop date.  This cannot
 * be represented in an interval but can be represented as an open ended OpenInterval.
 * 
 */
public class OpenInterval {
    private Instant start; // null implies this started at the beginning of time
    private Instant end; // null implies this stops at the end of time
    
    private static final Ordering<OpenInterval> comparator =
        Ordering.natural().nullsFirst().onResultOf(new Function<OpenInterval, Instant>() {
            public Instant apply(OpenInterval from) {
                return from.start;
            }
        });
    
    private static final Ordering<OpenInterval> endComparator =
        Ordering.natural().nullsLast().onResultOf(new Function<OpenInterval, Instant>() {
            public Instant apply(OpenInterval from) {
                return from.end;
            }
        });
    
    private OpenInterval(Instant start, Instant end) {
        this.start = start;
        this.end = end;
    }

    public boolean isOpenStart() {
        return start == null;
    }
    
    public boolean isOpenEnd() {
        return end == null;
    }
    
    public boolean isClosed() {
        return !isOpenStart() && !isOpenEnd();
    }
    
    public boolean isUnBounded() {
        return isOpenStart() && isOpenEnd();
    }
    
    public boolean isBounded() {
        return !isUnBounded();
    }
    
    public Interval toClosedInterval() {
        Validate.isTrue(isClosed());
        return new Interval(start, end);
    }
    
    public Instant getStart() {
        return start;
    }
    
    public Instant getEnd() {
        return end;
    }
    
    public boolean isStartsBefore(ReadableInstant instant) {
        if (isOpenStart()) {
            return true;
        } else {
            return start.isBefore(instant);
        }
    }
    
    public boolean isStartsAfter(ReadableInstant instant) {
        if (isOpenStart()) {
            return false;
        } else {
            return start.isAfter(instant);
        }
    }
    
    public boolean isBefore(OpenInterval interval) {
        if (interval == null) {
            return false;
        }
        if (this.isOpenEnd() || interval.isOpenStart()) {
            return false;
        } else {
            return this.end.isBefore(interval.start);
        }
    }
    
    public boolean isAfter(OpenInterval interval) {
        if (interval == null) {
            return false;
        }
        if (this.isOpenStart() || interval.isOpenEnd()) {
            return false;
        } else {
            return this.start.isAfter(interval.end);
        }
    }
    
    public boolean isEndsBefore(ReadableInstant instant) {
        if (isOpenEnd()) {
            return false;
        } else {
            return end.isBefore(instant);
        }
    }
    
    public boolean isEndsAfter(ReadableInstant instant) {
        if (isOpenEnd()) {
            return true;
        } else {
            return end.isAfter(instant);
        }
    }
    
    public boolean contains(ReadableInstant instant) {
        return !isStartsAfter(instant) && isEndsAfter(instant);
    }
    
    public boolean overlaps(OpenInterval b) {
        if (b == null) {
            return false;
        }
        return !isBefore(b) && !isAfter(b);
    }
    
    public OpenInterval overlap(OpenInterval b) {
        if (b == null) {
            return null;
        }
        if (this.isBefore(b) || this.isAfter(b)) {
            return null;
        }
        
        if (this.contains(b)) {
            return b;
        }
        
        if (b.contains(this)) {
            return this;
        }
        
        if (this.containsEndOf(b)) {
            return new OpenInterval(this.start, b.end);
        }
        
        if (this.containsStartOf(b)) {
            return new OpenInterval(b.start, this.end);
        }
        
        return null; // not sure this line is reachable
        
    }
    
    public static List<OpenInterval> intersection(List<List<OpenInterval>> temp) {
        if (temp.isEmpty()) {
            return ImmutableList.of();
        }
        // check input and order lists
        List<List<OpenInterval>> listOfSortedLists = Lists.newArrayListWithCapacity(temp.size());
        for (List<OpenInterval> list : temp) {
            List<OpenInterval> sortedCopy = comparator.sortedCopy(list);
            boolean overlap = overlapSorted(sortedCopy);
            if (overlap) {
                throw new IllegalArgumentException("inner list is not allowed to have overlaps: " + sortedCopy);
            }
            listOfSortedLists.add(sortedCopy);
        }
        
        Iterator<List<OpenInterval>> iterator = listOfSortedLists.iterator();
        List<OpenInterval> previous = iterator.next();
        while (iterator.hasNext()) {
            List<OpenInterval> current = iterator.next();
            previous = intersection(previous, current);
        }
        
        return previous;
    }

    private static List<OpenInterval> intersection(List<OpenInterval> listA, List<OpenInterval> listB) {
        List<OpenInterval> result = Lists.newArrayListWithExpectedSize(listA.size() + listB.size());

        // we know that both lists are sorted
        PeekingIterator<OpenInterval> a = Iterators.peekingIterator(listA.iterator());
        PeekingIterator<OpenInterval> b = Iterators.peekingIterator(listB.iterator());
        while (a.hasNext() && b.hasNext()) {
            OpenInterval intervalA = a.peek();
            OpenInterval intervalB = b.peek();
            OpenInterval overlap = intervalA.overlap(intervalB);
            if (overlap != null) {
                result.add(overlap);
            }
            
            // since the inputs are sorted by start, iterate based on end
            int code = endComparator.compare(intervalA, intervalB);
            if (code <= 0) {
                a.next();
            }

            if (code >= 0) {
                b.next();
            }
        }
        
        return result;
    }

    private static boolean overlapSorted(Iterable<OpenInterval> sortedCopy) {
        Iterator<OpenInterval> iterator = sortedCopy.iterator();
        if (!iterator.hasNext()) {
            return false;
        }
        
        OpenInterval previous = iterator.next();
        while (iterator.hasNext()) {
            OpenInterval current = iterator.next();
            if (previous.overlaps(current)) {
                return true;
            }
            previous = current;
        }
        
        return false;
    }

    public List<OpenInterval> invert() {
        if (isUnBounded()) {
            return ImmutableList.of();
        } else if (isClosed()) {
            OpenInterval left = OpenInterval.createOpenStart(start);
            OpenInterval right = OpenInterval.createOpenEnd(end);
            return ImmutableList.of(left, right);
        } else if (isOpenStart()) {
            OpenInterval right = OpenInterval.createOpenEnd(end);
            return ImmutableList.of(right);
        } else if (isOpenEnd()) {
            OpenInterval left = OpenInterval.createOpenStart(start);
            return ImmutableList.of(left);
        } else {
            throw new IllegalStateException();
        }
    }
    
    private boolean containsEndOf(OpenInterval b) {
        // keep in mind end is exclusive
        if (b.isOpenEnd()) {
            return this.isOpenEnd();
        } else {
            return b.end.equals(this.end) || this.contains(b.end);
        }
    }
    
    private boolean containsStartOf(OpenInterval b) {
        // keep in mind start is inclusive
        if (b.isOpenStart()) {
            return this.isOpenStart();
        } else {
            return this.contains(b.start);
        }
    }
    
    public boolean contains(OpenInterval b) {
        if (b == null) {
            return false;
        }
        return this.containsStartOf(b) && this.containsEndOf(b);
    }

    public static OpenInterval createClosed(ReadableInstant start, ReadableInstant end) {
        return new OpenInterval(start.toInstant(), end.toInstant());
    }
    public static OpenInterval createOpenEnd(ReadableInstant start) {
        return new OpenInterval(start.toInstant(), null);
    }
    public static OpenInterval createOpenStart(ReadableInstant end) {
        return new OpenInterval(null, end.toInstant());
    }
    public static OpenInterval createUnBounded() {
        return new OpenInterval(null, null);
    }

    public static OpenInterval createClosed(Date start, Date end) {
        return new OpenInterval(new Instant(start), new Instant(end));
    }
    public static OpenInterval createOpenEnd(Date start) {
        return new OpenInterval(new Instant(start), null);
    }
    public static OpenInterval createOpenStart(Date end) {
        return new OpenInterval(null, new Instant(end));
    }
    
    public OpenInterval withCurrentEnd() {
        return new OpenInterval(start, new Instant());
    }
    
    public OpenInterval withCurrentEndIfOpen() {
        if (isOpenEnd()) {
            return new OpenInterval(start, new Instant());
        } else {
            return this;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
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
        OpenInterval other = (OpenInterval) obj;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (start == null) {
            if (other.start != null)
                return false;
        } else if (!start.equals(other.start))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return (isOpenStart() ? "open" : start) + " to " + (isOpenEnd() ? "open" : end);
    }
    
}
