package com.cannontech.services.smartNotification.service.impl.email;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Builds up text Strings for Device Data Monitor notification messages, based on the parameters passed in.
 */
public class DeviceDataMonitorEmailBuilder extends SmartNotificationEmailBuilder {
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private IDatabaseCache serverDatabaseCache;
    
    @Override
    public SmartNotificationEventType getSupportedType() {
        return SmartNotificationEventType.DEVICE_DATA_MONITOR;
    }
    
    @Override
    protected Object[] getBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity, int eventPeriodMinutes) {
        List<Object> argumentList = new ArrayList<>();
        if (events.size() == 1) {
            argumentList = getSingleEventBodyArguments(events.get(0), verbosity);
        } else {
            argumentList = getMultiEventBodyArguments(events, verbosity, eventPeriodMinutes);
        }
        return argumentList.toArray();
    }
    
    /**
     * Builds a list of arguments for the body text. 
     * 0 - device name
     * 1 - violation state
     * 2 - monitor name
     * 3 - url
     */
    private List<Object> getSingleEventBodyArguments(SmartNotificationEvent event, SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();
        
        int paoId = DeviceDataMonitorEventAssembler.getPaoId(event.getParameters());
        String paoName = serverDatabaseCache.getAllPaosMap().get(paoId).getPaoName();
        argumentList.add(paoName);
        
        String violationState = DeviceDataMonitorEventAssembler.getState(event.getParameters()).name();
        String violationText = messageSourceAccessor.getMessage("yukon.web.modules.smartNotifications.DEVICE_DATA_MONITOR." + violationState);
        argumentList.add(violationText);
        
        String monitorName = DeviceDataMonitorEventAssembler.getMonitorName(event.getParameters());
        argumentList.add(monitorName);
        
        int monitorId = DeviceDataMonitorEventAssembler.getMonitorId(event.getParameters());
        argumentList.add(getUrl("deviceDataMonitor/" + monitorId));
        
        return argumentList;
    }
    
    /**
     * Builds a list of arguments for the body text.
     * 0 - number of events
     * 1 - monitor name
     * 2 - time period minutes
     * 3 - url
     * 4 - event details list
     */
    private List<Object> getMultiEventBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity, 
                                                    int eventPeriodMinutes) {
        List<Object> argumentList = new ArrayList<>();
        argumentList.add(events.size());
        
        //Get the monitor name from the first event, assuming that all events are for the same monitor
        String monitorName = DeviceDataMonitorEventAssembler.getMonitorName(events.get(0).getParameters());
        argumentList.add(monitorName);
        
        argumentList.add(eventPeriodMinutes);
        
        //Get the monitorId from the first event, assuming that all events are for the same monitor
        int monitorId = DeviceDataMonitorEventAssembler.getMonitorId(events.get(0).getParameters());
        argumentList.add(getUrl("deviceDataMonitor/" + monitorId));
        
        if (verbosity == SmartNotificationVerbosity.EXPANDED) {
            argumentList.add(getEventsDetailText(events));
        }
        return argumentList;
    }
    
    /**
     * Builds a String containing one line per event, with each line displaying the device name, violation state, and time.
     */
    private String getEventsDetailText(List<SmartNotificationEvent> events) {
        DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormattingService.DateFormatEnum.FULL, YukonUserContext.system);
        
        StringBuilder builder = new StringBuilder();
        builder.append("   ");
        for (SmartNotificationEvent event : events) {
            
            int paoId = DeviceDataMonitorEventAssembler.getPaoId(event.getParameters());
            String paoName = serverDatabaseCache.getAllPaosMap().get(paoId).getPaoName();
            builder.append(paoName).append(" - ");
            
            String violationState = DeviceDataMonitorEventAssembler.getState(event.getParameters()).name();
            String violationText = messageSourceAccessor.getMessage("yukon.web.modules.smartNotifications.DEVICE_DATA_MONITOR." + violationState);
            builder.append(violationText).append(" - ");
            
            String date = dateFormatter.format(event.getTimestamp().toDate());
            builder.append(date).append("\n   ");
        }
        return builder.toString();
    }
    
    /**
     * Builds a list of arguments for the subject line
     * Multi
     * 0 - Data Monitor Name
     * 1 - Number of events
     * Single
     * 0 - Data Monitor name
     * 1 - device name
     * 2 - violation status
     */
    @Override
    protected Object[] getSubjectArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();
        
        //Get the monitor name from the first event, assuming that all events are for the same monitor
        String monitorName = DeviceDataMonitorEventAssembler.getMonitorName(events.get(0).getParameters());
        argumentList.add(monitorName);
        
        if (events.size() > 1) {
            argumentList.add(events.size());
        } else {
            SmartNotificationEvent event = events.get(0);
            
            int paoId = DeviceDataMonitorEventAssembler.getPaoId(event.getParameters());
            String paoName = serverDatabaseCache.getAllPaosMap().get(paoId).getPaoName();
            argumentList.add(paoName);
            
            String violationState = DeviceDataMonitorEventAssembler.getState(event.getParameters()).name();
            String violationText = messageSourceAccessor.getMessage("yukon.web.modules.smartNotifications.DEVICE_DATA_MONITOR." + violationState);
            argumentList.add(violationText);
        }
        return argumentList.toArray();
    }
}
