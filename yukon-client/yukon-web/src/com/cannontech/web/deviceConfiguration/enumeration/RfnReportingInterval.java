package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;
import org.springframework.stereotype.Component;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

@Component
public final class RfnReportingInterval extends RfnInterval implements DeviceConfigurationInputEnumeration {

    //  5m, 15m, 30m, 1h
    private static final Integer[] highFrequencyIntervals = { 5, 15, 30, 60 };
    //  2h, 4h, 6h, 12h, 1d, 2d
    private static final Integer[] minuteIntervals = { 2*60, 4*60, 6*60, 12*60, 24*60, 2*24*60 };

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        return createIntervalInputOptions(userContext, highFrequencyIntervals, minuteIntervals);
    }

    @Override
    public String getEnumOptionName() {
        return "ReportingInterval";
    }
}