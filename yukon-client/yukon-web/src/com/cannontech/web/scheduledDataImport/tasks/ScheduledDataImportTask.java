package com.cannontech.web.scheduledDataImport.tasks;

import com.cannontech.jobs.support.YukonTaskBase;

/**
 * A data import file task that will import the file based on import path
 * and process the imported file and generate an error output file in case of
 * any error while processing.
 */
public class ScheduledDataImportTask extends YukonTaskBase {

    private String scheduleName;
    private String importPath;
    private String errorFileOutputPath;
    private String importType;

    @Override
    public void start() {
        // TODO - Need to implement the file import and processing Logic here
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
