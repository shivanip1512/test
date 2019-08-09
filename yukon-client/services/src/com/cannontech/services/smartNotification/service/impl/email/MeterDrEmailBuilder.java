package com.cannontech.services.smartNotification.service.impl.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cannontech.common.smartNotification.model.MeterDrEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;

/**
 * Builds up text Strings for Meter Demand Response notification messages, based on the parameters passed in.
 */
public class MeterDrEmailBuilder extends SmartNotificationEmailBuilder {
    @Override
    public SmartNotificationEventType getSupportedType() {
        return SmartNotificationEventType.METER_DR;
    }

    @Override
    protected Object[] getBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity,
            int eventPeriodMinutes) {
        List<Object> argumentList = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.putAll(events.get(0).getParameters());
        // Add program name to body arg 0
        argumentList.add(params.get(MeterDrEventAssembler.PROGRAM_NAME));
        params.remove(MeterDrEventAssembler.PROGRAM_NAME);
        // Add counts of each status to body arg 1
        argumentList.add(params.entrySet().stream()
            .map(entry -> entry.getValue() + " " + messageSourceAccessor.getMessage(DrMeterControlStatus.valueOf(entry.getKey()).getFormatKey()))
            .collect(Collectors.joining("\n")));
        return argumentList.toArray();
    }

    @Override
    protected Object[] getSubjectArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();
        argumentList.add(MeterDrEventAssembler.getProgramName(events.get(0).getParameters()));
        return argumentList.toArray();
    }
}
