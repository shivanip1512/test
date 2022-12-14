package com.cannontech.web.deviceConfiguration.enumeration;

import org.springframework.stereotype.Component;

@Component
public final class DisconnectDemandInterval extends DemandInterval {

    private static final Integer[] intervals = { 5, 10, 15 };

    @Override
    protected Integer[] getIntervals() {
        return intervals;
    }

    @Override
    public String getEnumOptionName() {
        return "DisconnectDemandInterval";
    }
}