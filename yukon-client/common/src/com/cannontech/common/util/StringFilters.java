package com.cannontech.common.util;

import java.util.regex.Pattern;

import com.google.common.base.Predicate;

public class StringFilters {
    public static Predicate<String> getRegExFilter(final Pattern pattern) {
        return new Predicate<String>() {
            public boolean apply(String input) {
                return pattern.matcher(input).lookingAt();
            }
        };
    }
}
