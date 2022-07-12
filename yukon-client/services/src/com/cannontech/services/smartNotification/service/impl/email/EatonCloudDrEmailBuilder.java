package com.cannontech.services.smartNotification.service.impl.email;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.smartNotification.model.EatonCloudDrEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.common.util.WebserverUrlResolver;

/**
 * Builds up text Strings for Meter Demand Response notification messages, based on the parameters passed in.
 */
public class EatonCloudDrEmailBuilder extends SmartNotificationEmailBuilder {
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private WebserverUrlResolver webserverUrlResolver;
    private Integer failureNotificationPercent;
       
    @Override
    public SmartNotificationEventType getSupportedType() {
        return SmartNotificationEventType.EATON_CLOUD_DR;
    }

    @Override
    protected Object[] getBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity,
            int eventPeriodMinutes) {
        List<Object> argumentList = new ArrayList<>();
        argumentList.add(EatonCloudDrEventAssembler.getStringValue(events.get(0).getParameters(), EatonCloudDrEventAssembler.LM_PROGRAM));
        argumentList.add(EatonCloudDrEventAssembler.getStringValue(events.get(0).getParameters(), EatonCloudDrEventAssembler.LM_GROUP));
        if(failureNotificationPercent == null) {
            failureNotificationPercent = configurationSource.getInteger(
                    MasterConfigInteger.EATON_CLOUD_NOTIFICATION_COMMAND_FAILURE_PERCENT, 25); 
        }
        argumentList.add(100 - failureNotificationPercent);
        argumentList.add(EatonCloudDrEventAssembler.getStringValue(events.get(0).getParameters(), EatonCloudDrEventAssembler.LM_PROGRAM));
        argumentList.add(EatonCloudDrEventAssembler.getStringValue(events.get(0).getParameters(), EatonCloudDrEventAssembler.LM_GROUP));
        int total = EatonCloudDrEventAssembler.getIntValue(events.get(0).getParameters(), EatonCloudDrEventAssembler.TOTAL_DEVICES);
        int failed = EatonCloudDrEventAssembler.getIntValue(events.get(0).getParameters(), EatonCloudDrEventAssembler.TOTAL_FAILED);
        int success = total - failed;
        argumentList.add(total);
        argumentList.add(Math.round((success * 100) / total));
        argumentList.add(Math.round((failed * 100) / total));
        argumentList.add(EatonCloudDrEventAssembler.getIntValue(events.get(0).getParameters(), EatonCloudDrEventAssembler.CONTROL));      
        return argumentList.toArray();
    }

    @Override
    protected Object[] getSubjectArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();
        argumentList.add(EatonCloudDrEventAssembler.getStringValue(events.get(0).getParameters(), EatonCloudDrEventAssembler.LM_PROGRAM));
        argumentList.add(EatonCloudDrEventAssembler.getIntValue(events.get(0).getParameters(), EatonCloudDrEventAssembler.CONTROL));
        return argumentList.toArray();
    }
}
