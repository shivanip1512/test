package com.cannontech.services.smartNotification.service.impl.email;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.smartNotification.model.InfrastructureWarningsParametersAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Builds up text Strings for Infrastructure Warnings notification messages, based on the parameters passed in.
 */
public class InfrastructureWarningsEmailBuilder extends SmartNotificationEmailBuilder {
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private IDatabaseCache serverDatabaseCache;
    
    @Override
    public SmartNotificationEventType getSupportedType() {
        return SmartNotificationEventType.INFRASTRUCTURE_WARNING;
    }
    
    @Override
    protected Object[] getBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity, 
                                        int eventPeriodMinutes) {
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
     * 0 - Device
     * 1 - Warning type
     * 2 - url
     */
    private List<Object> getSingleEventBodyArguments(SmartNotificationEvent event, SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();
        
        int paoId = InfrastructureWarningsParametersAssembler.getPaoId(event.getParameters());
        String paoName = serverDatabaseCache.getAllPaosMap().get(paoId).getPaoName();
        argumentList.add(paoName);
        
        InfrastructureWarningType warningType = InfrastructureWarningsParametersAssembler.getWarningType(event.getParameters());
        String warningTypeString = messageSourceAccessor.getMessage(warningType);
        argumentList.add(warningTypeString);
        
        argumentList.add(getUrl("infrastructureWarnings"));
        
        return argumentList;
    }
    
    /**
     * Builds a list of arguments for the body text.
     * 0 - number of events
     * 1 - time period minutes
     * 2 - url
     * 3 - event details list
     */
    private List<Object> getMultiEventBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity, 
                                                    int eventPeriodMinutes) {
        List<Object> argumentList = new ArrayList<>();
        
        argumentList.add(events.size());
        argumentList.add(eventPeriodMinutes);
        argumentList.add(getUrl("infrastructureWarnings"));
        
        if (verbosity == SmartNotificationVerbosity.EXPANDED) {
            argumentList.add(getEventsDetailText(events));
        }
        
        return argumentList;
    }
    
    /**
     * Builds a String containing one line per event, with each line displaying the device name, warning type and time.
     */
    private String getEventsDetailText(List<SmartNotificationEvent> events) {
        DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormattingService.DateFormatEnum.FULL, YukonUserContext.system);
        
        StringBuilder builder = new StringBuilder();
        builder.append("   ");
        for (SmartNotificationEvent event : events) {
            
            int paoId = InfrastructureWarningsParametersAssembler.getPaoId(event.getParameters());
            String paoName = serverDatabaseCache.getAllPaosMap().get(paoId).getPaoName();
            builder.append(paoName).append(" - ");
            
            InfrastructureWarningType warningType = InfrastructureWarningsParametersAssembler.getWarningType(event.getParameters());
            InfrastructureWarningSeverity severity = InfrastructureWarningsParametersAssembler.getWarningSeverity(event.getParameters());
            String warningKey = warningType.getFormatKey() + "." + severity.name();
            Object[] warningArgs = InfrastructureWarningsParametersAssembler.getWarningArguments(event.getParameters());
            String warningTypeString = messageSourceAccessor.getMessage(warningKey, warningArgs);
            builder.append(warningTypeString).append(" - ");
            
            String date = dateFormatter.format(event.getTimestamp().toDate());
            builder.append(date).append("\n   ");
        }
        return builder.toString();
    }
    
    /**
     * Builds a list of arguments for the subject line
     * Multi
     * 0 - number of events
     * Single
     * 0 - device name
     * 1 - warning type
     */
    @Override
    protected Object[] getSubjectArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();
        if (events.size() == 1) {
            SmartNotificationEvent event = events.get(0);
            
            int paoId = InfrastructureWarningsParametersAssembler.getPaoId(event.getParameters());
            String paoName = serverDatabaseCache.getAllPaosMap().get(paoId).getPaoName();
            argumentList.add(paoName);
            
            InfrastructureWarningType warningType = InfrastructureWarningsParametersAssembler.getWarningType(event.getParameters());
            String warningTypeString = messageSourceAccessor.getMessage(warningType);
            argumentList.add(warningTypeString);
        } else {
            argumentList.add(events.size());
        }
        return argumentList.toArray();
    }
}
