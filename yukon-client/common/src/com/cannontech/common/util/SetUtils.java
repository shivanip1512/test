package com.cannontech.common.util;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class SetUtils {
    
    /**
     * Returns a SetView of the input Set as if it did not contain the elementToRemove.
     * Note that this is not a copy, if the input Set changes, the returned view will
     * reflect this change.
     * @param input Set, will not be modified
     * @param elementToRemove, must not be null
     * @return SetView (see its documentation for details)
     */
    public static <E> Sets.SetView<E> minusOne(Set<E> input, E elementToRemove) {
        return Sets.difference(input, ImmutableSet.of(elementToRemove));
    }
    
    /**
     * Returns a SetView of the input Set as if it additionally contained the elementToAdd.
     * Note that this is not a copy, if the input Set changes, the returned view will
     * reflect this change.
     * @param input Set, will not be modified
     * @param elementToAdd, must not be null
     * @return SetView (see its documentation for details)
     */
    public static <E> Sets.SetView<E> plusOne(Set<E> input, E elementToAdd) {
        return Sets.union(input, ImmutableSet.of(elementToAdd));
    }

}
