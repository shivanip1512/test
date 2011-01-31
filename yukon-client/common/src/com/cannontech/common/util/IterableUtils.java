package com.cannontech.common.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class IterableUtils {
    public static int guessSize(Iterable<?> iterable, int defaultSize) {
        if (iterable instanceof Collection<?>) {
            return ((Collection<?>) iterable).size();
        } else {
            return defaultSize;
        }
    }
    public static int guessSize(Iterable<?> iterable) {
        return guessSize(iterable, 16);
    }

    /**
     * Get an iterator that will iterate over the passed in iterables alternately.  For example,
     * if you pass in two lists [a, b, c] and [1, 2, 3, 4, 5], the returned iterable will return
     * [a, 1, b, 2, c, 3, 4, 5]
     */
    public static <T> Iterable<T> collate(final Iterable<? extends Iterable<T>> iterables) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private List<Iterator<T>> iters = Lists.newArrayList();
                    private int cursor = 0;
                    {
                        for (Iterable<T> iterable : iterables) {
                            iters.add(iterable.iterator());
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        for (Iterator<T> iter : iters) {
                            if (iter.hasNext()) {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public T next() {
                        while (cursor < iters.size() && !iters.get(cursor).hasNext()) {
                            cursor++;
                        }
                        if (cursor >= iters.size()) {
                            cursor = 0;
                            while (cursor < iters.size() && !iters.get(cursor).hasNext()) {
                                cursor++;
                            }
                        }
                        if (cursor >= iters.size()) {
                            throw new NoSuchElementException();
                        }
                        return iters.get(cursor++).next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("collated iterable does not support remove");
                    }};
            }};
    }

    public static <T> Iterable<T> clipped(final Iterable<T> iterable, final int maxItems) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private Iterator<T> baseIter = iterable.iterator();
                    private int itemsReturned = 0;

                    @Override
                    public boolean hasNext() {
                        return itemsReturned < maxItems && baseIter.hasNext();
                    }

                    @Override
                    public T next() {
                        if (itemsReturned >= maxItems) {
                            throw new NoSuchElementException();
                        }
                        itemsReturned++;
                        return baseIter.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("clipped iterable does not support remove");
                    }};
            }};
    }
    
    /**
     * Use this when you have an Iterable of type A, and you know all of its elements can be casted to type B, 
     * and you want an Iterable of type B. This method blindly casts the objects, so any exceptions will bubble up.
     * 
     * Compare this to Google's {@link Iterables#filter(Iterable, Class)} which takes the same arguments, but 
     * will silently remove any items that are not castable.
     * @param <T>
     * @param questionableIterable
     * @param desiredClass
     * @return
     */
    public static <T> Iterable<T> castIterable(final Iterable<?> questionableIterable, final Class<T> desiredClass) throws ClassCastException {
        
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                final Iterator<?> iterator = questionableIterable.iterator();
                return new Iterator<T>() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public T next() {
                        return desiredClass.cast(iterator.next());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                    
                };
            }
        };
    }
}
