package com.cannontech.common.smartNotification.service.impl.email;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;

public class InfrastructureWarningsEmailBuilder extends SmartNotificationEmailBuilder {

    @Override
    public SmartNotificationEventType getSupportedType() {
        return SmartNotificationEventType.INFRASTRUCTURE_WARNING;
    }
    
    @Override
    protected Object[] getBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity, 
                                        int eventPeriodMinutes) {
        List<Object> argumentList = new ArrayList<>();
        if (events.size() == 1) {
            //TODO single arguments
        } else {
            //TODO multi arguments
        }
        return argumentList.toArray();
    }
    
    @Override
    protected Object[] getSubjectArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();
        if (events.size() == 1) {
            //TODO single arguments
        } else {
            //TODO multi arguments
        }
        return argumentList.toArray();
    }
}
