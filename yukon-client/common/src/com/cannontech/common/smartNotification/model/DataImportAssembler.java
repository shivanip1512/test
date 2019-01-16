package com.cannontech.common.smartNotification.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.scheduledFileImport.DataImportWarning;
import com.cannontech.common.scheduledFileImport.ScheduledImportType;


/**
 * This class is used to convert DataImportWarning to SmartNotificationEvent.
 * Also it consist of method which is used by DataImportEmailBuilder to get 
 * required information to build the email subject and body content.
 *
 */
public class DataImportAssembler {

    public static final String TASK_NAME = "taskName";
    public static final String FILES_WITH_ERROR = "filesWithError";
    public static final String SUCCESS_FILE_COUNT = "successFileCount";
    public static final String IMPORT_TYPE = "importType";

    public static SmartNotificationEvent assemble(Instant now, DataImportWarning warning) {
        SmartNotificationEvent event = new SmartNotificationEvent(now);
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put(TASK_NAME, warning.getTaskName());
        if (warning.getFilesWithError() != null && !"".equalsIgnoreCase(warning.getFilesWithError().trim()))
        parameters.put(FILES_WITH_ERROR, warning.getFilesWithError());
        parameters.put(SUCCESS_FILE_COUNT, warning.getSuccessFileCount());
        parameters.put(IMPORT_TYPE, warning.getImportType());
        event.setParameters(parameters);
        return event;
    }

    public static ScheduledImportType getImportType(Map<String, Object> parameters) {
        return ScheduledImportType.fromName(parameters.get(IMPORT_TYPE).toString());
    }

    public static String getTaskName(Map<String, Object> parameters) {
        return parameters.get(TASK_NAME).toString();
    }

    public static Object getFilesWithError(Map<String, Object> parameters) {
        return parameters.get(FILES_WITH_ERROR);
    }

}
