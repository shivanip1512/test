package com.cannontech.web.deviceConfiguration.enumeration;

import org.springframework.stereotype.Component;

@Component
public final class ProfileInterval extends Interval {

    private final Integer[] intervals = { 5, 15, 30, 60 };

    @Override
    protected Integer[] getIntervals() {
        return intervals;
    }

    @Override
    public String getEnumOptionName() {
        return "ProfileInterval";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}