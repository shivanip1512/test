package com.cannontech.common.util;

import java.util.Collection;

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
}
