package com.cannontech.web.deviceConfiguration.enumeration;

import com.cannontech.core.service.durationFormatter.DurationFormat;

public abstract class RfnChannelDataInterval extends Interval {

    protected abstract Integer[] getIntervals();

    @Override
    protected DurationFormat getDurationFormat() {
        return DurationFormat.DHMS_REDUCED;
    };
}