package com.cannontech.services.smartNotification.service.impl.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cannontech.common.scheduledFileImport.ScheduledImportType;
import com.cannontech.common.smartNotification.model.DataImportAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;

/**
 * Builds up email subject and body content for Data import notification messages.
 */
public class DataImportEmailBuilder extends SmartNotificationEmailBuilder {
    
    @Override
    public SmartNotificationEventType getSupportedType() {
        return SmartNotificationEventType.ASSET_IMPORT;
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
     * Builds a list of arguments for the single event body text.
     * 0 - Import Type
     * 1 - Task Name
     * 2 - Count of files with error
     * 3 - Name of file with error (Only for EXPANDED verbosity)
     * 4 - Url for viewing Asset Import SmartNotification events
     */
    private List<Object> getSingleEventBodyArguments(SmartNotificationEvent event,
            SmartNotificationVerbosity verbosity) {

        List<Object> argumentList = new ArrayList<>();
        ScheduledImportType importType = DataImportAssembler.getImportType(event.getParameters());
        argumentList.add(messageSourceAccessor.getMessage(importType.getFormatKey()));
        argumentList.add(getScheduleOrManualTaskName(event.getParameters()));
        List<String> filesWithError = getFilesWithError(event.getParameters());
        argumentList.add(filesWithError.size());
        if (verbosity == SmartNotificationVerbosity.EXPANDED) {
            String errorBody = filesWithError.stream().map(file -> messageSourceAccessor.getMessage(
                "yukon.web.modules.smartNotifications.ASSET_IMPORT.EXPANDED.body.text", file)).collect(
                    Collectors.joining("\n"));
            argumentList.add(errorBody);
        }
        argumentList.add(getUrl("assetImport"));

        return argumentList;
    }
    
    /**
     * Builds a list of arguments for the multiple event body text.
     * 0 - Number of events
     * 1 - Time period minutes
     * 2 - Task name & File name with error (Only for EXPANDED verbosity)
     * 3 - Url for viewing Asset Import SmartNotification events.
     */
    private List<Object> getMultiEventBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity, 
                                                    int eventPeriodMinutes) {

        List<Object> argumentList = new ArrayList<>();
        argumentList.add(events.size());
        argumentList.add(eventPeriodMinutes);
        StringBuilder builder = new StringBuilder();
        builder.append(" ");
        if (verbosity == SmartNotificationVerbosity.EXPANDED) {
            for (SmartNotificationEvent event : events) {
                List<Object> bodyArgs = new ArrayList<>();
                bodyArgs.add(getScheduleOrManualTaskName(event.getParameters()));
                List<String> filesWithError = getFilesWithError(event.getParameters());
                bodyArgs.add(filesWithError.size());
                String errorBody = filesWithError.stream().map(file -> messageSourceAccessor.getMessage(
                    "yukon.web.modules.smartNotifications.ASSET_IMPORT.EXPANDED.body.text", file)).collect(
                        Collectors.joining("\n"));
                bodyArgs.add(errorBody);
                String body = messageSourceAccessor.getMessage(
                    "yukon.web.modules.smartNotifications.ASSET_IMPORT.multi.EXPANDED.body.text", bodyArgs.toArray());
                builder.append(body);
                builder.append("\n ");
            }
        }
        argumentList.add(builder.toString());
        argumentList.add(getUrl("assetImport"));
        return argumentList;
    }

    /**
     * Builds a list of arguments for the subject line
     * Multi
     * 0 - Number of events
     * Single
     * 0 - Import type
     * 1 - Count of files with error
     */
    @Override
    protected Object[] getSubjectArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity) {
        List<Object> argumentList = new ArrayList<>();

        if (events.size() == 1) {
            SmartNotificationEvent event = events.get(0);
            ScheduledImportType importType = DataImportAssembler.getImportType(event.getParameters());
            argumentList.add(messageSourceAccessor.getMessage(importType.getFormatKey()));
            argumentList.add(getFilesWithError(event.getParameters()).size());
        } else {
            argumentList.add(events.size());
        }

        return argumentList.toArray();
    }

    /**
     * This method will return the name of file which get error while processing.
     */
    private List<String> getFilesWithError(Map<String, Object> parameters) {
        Object filesWithError = DataImportAssembler.getFilesWithError(parameters);
        List<String> files = new ArrayList<>();
        if (filesWithError != null) {
            files = Arrays.asList(filesWithError.toString().split(" , "));
        }
        return files;
    }

    /**
     * This method will return the task name.
     */
    private String getScheduleOrManualTaskName(Map<String, Object> parameters) {
        return DataImportAssembler.getTaskName(parameters);
    }
}
