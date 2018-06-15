package com.cannontech.web.deviceConfiguration.enumeration;

import org.springframework.stereotype.Component;

@Component
public final class MctDemandInterval extends DemandInterval {

    private final Integer[] intervals = { 5, 15, 30, 60 };

    @Override
    protected Integer[] getIntervals() {
        return intervals;
    }

    @Override
    public String getEnumOptionName() {
        return "MctDemandInterval";
    }
}