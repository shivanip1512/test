package com.cannontech.web.scheduledDataImport.tasks;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.scheduledFileImport.DataImportWarning;
import com.cannontech.common.smartNotification.model.DataImportAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.web.dataImport.DataImportHelper;
import com.cannontech.web.stars.dr.operator.importAccounts.AccountImportResult;

/**
 * A data import file task that will import the file based on import path
 * and process the imported file and generate an error output file in case of
 * any error while processing.
 */
public class ScheduledDataImportTask extends YukonTaskBase {

    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;

    private String scheduleName;
    private String importPath;
    private String errorFileOutputPath;
    private String importType;

    @Override
    public void start() {
        // TODO - Need to implement the file import and processing Logic here
        AccountImportResult importResult = new AccountImportResult();// TODO :Will be removed after YUK-19061 implementation
        List<DataImportWarning> dataImportwarning =
            DataImportHelper.getDataImportWarning(getJob().getJobGroupId(), getScheduleName(), getImportType(), importResult);
        List<SmartNotificationEvent> smartNotificationEvent =
                dataImportwarning.stream().map(importWarning -> DataImportAssembler.assemble(Instant.now(), importWarning))
                                          .collect(Collectors.toList());
        smartNotificationEventCreationService.send(SmartNotificationEventType.ASSET_IMPORT, smartNotificationEvent);
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
