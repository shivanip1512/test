package com.cannontech.services.smartNotification.service.impl.email;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;

public class YukonWatchdogEmailBuilder extends SmartNotificationEmailBuilder{
    
    // TODO: This is temporary code, this will have to be replaced  
    @Override
    public SmartNotificationEventType getSupportedType() {
        return SmartNotificationEventType.YUKON_WATCHDOG;
    }
    
    @Override
    protected Object[] getBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity, 
                                        int eventPeriodMinutes) {
        List<Object> argumentList = new ArrayList<>();
        if (events.size() == 1) {
            argumentList = getSingleEventBodyArguments(events.get(0), verbosity);
        } else {
            //argumentList = getMultiEventBodyArguments(events, verbosity, eventPeriodMinutes);
        }
        return argumentList.toArray();
    }


private List<Object> getSingleEventBodyArguments(SmartNotificationEvent event, SmartNotificationVerbosity verbosity) {
    List<Object> argumentList = new ArrayList<>();
    
    
    argumentList.add("Yukon Watch Dog - 1");
    
    return argumentList;
}

@Override
protected Object[] getSubjectArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity) {
    List<Object> argumentList = new ArrayList<>();
    argumentList.add("Yukon Watch Dog - 2");
    return argumentList.toArray();
}

}
