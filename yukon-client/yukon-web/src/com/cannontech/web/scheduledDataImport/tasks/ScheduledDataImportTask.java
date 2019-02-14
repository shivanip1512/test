package com.cannontech.web.scheduledDataImport.tasks;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.scheduledFileImport.DataImportWarning;
import com.cannontech.common.scheduledFileImport.ScheduledImportType;
import com.cannontech.common.smartNotification.model.DataImportAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.web.dataImport.DataImportHelper;
import com.cannontech.web.scheduledDataImport.ScheduledDataImportResult;
import com.cannontech.web.scheduledDataImport.service.ScheduledImportService;

/**
 * A data import file task that will import the file based on import path
 * and process the imported file and generate an error output file in case of
 * any error while processing.
 */
public class ScheduledDataImportTask extends YukonTaskBase {

    private static final Logger log = YukonLogManager.getLogger(ScheduledDataImportTask.class);

    @Autowired private ScheduledImportService scheduledImportService;
    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;

    private String scheduleName;
    private String importPath;
    private String errorFileOutputPath;
    private String importType;

    @Override
    public void start() {
        File importDir = new File(getImportPath());
        if (importDir.exists()) {
            if (ScheduledImportType.fromImportTypeMap(getImportType()) == ScheduledImportType.ASSET_IMPORT) {
                log.info("Initiate Asset Import Task");
                ScheduledDataImportResult result = scheduledImportService.initiateImport(scheduleName, importPath, errorFileOutputPath);
                sendSmartNotification(SmartNotificationEventType.ASSET_IMPORT, result.getErrorFiles(),
                    result.getSuccessFiles().size());
            }
        } else {
            log.warn("No directory found for Import Path " + importPath);
        }

    }

    private void sendSmartNotification(SmartNotificationEventType eventType, List<String> errorFiles,
            Integer successFileCount) {
        if (!errorFiles.isEmpty() || successFileCount > 0) {
            log.info("Sending smart notification messages for asset import");
            List<DataImportWarning> dataImportwarning = DataImportHelper.getDataImportWarning(getJob().getJobGroupId(),
                getScheduleName(), getImportType(), errorFiles, successFileCount);
            List<SmartNotificationEvent> smartNotificationEvent = dataImportwarning.stream().map(
                importWarning -> DataImportAssembler.assemble(Instant.now(), importWarning)).collect(
                    Collectors.toList());
            smartNotificationEventCreationService.send(eventType, smartNotificationEvent);
        }

    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }

    public String getErrorFileOutputPath() {
        return errorFileOutputPath;
    }

    public void setErrorFileOutputPath(String errorFileOutputPath) {
        this.errorFileOutputPath = errorFileOutputPath;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }
}
