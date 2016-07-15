package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;

@Component
public class TableReadInterval implements DeviceConfigurationInputEnumeration {

    @Autowired private DurationFormattingService durationService;

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        List<InputOption> readIntervals = new ArrayList<>();

        for (int interval = 1; interval <= 30; interval++) {
            readIntervals.add( new InputOption(Integer.toString(interval * 15), 
                    durationService.formatDuration(interval * 15, TimeUnit.SECONDS, DurationFormat.DHMS_REDUCED, userContext)));
        }

        return readIntervals;
    }

    @Override
    public String getEnumOptionName() {
        return "TableReadInterval";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.CHOSEN;
    }
}