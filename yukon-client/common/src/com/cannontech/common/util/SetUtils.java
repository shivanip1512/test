package com.cannontech.common.util;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class SetUtils {
    
    public static <E> Sets.SetView<E> minusOne(Set<E> input, E elementToRemove) {
        return Sets.difference(input, ImmutableSet.of(elementToRemove));
    }
    
    public static <E> Sets.SetView<E> plusOne(Set<E> input, E elementToAdd) {
        return Sets.union(input, ImmutableSet.of(elementToAdd));
    }

}
