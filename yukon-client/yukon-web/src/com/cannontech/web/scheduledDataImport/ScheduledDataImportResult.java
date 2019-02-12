package com.cannontech.web.scheduledDataImport;

import java.util.ArrayList;
import java.util.List;

public class ScheduledDataImportResult {

    private List<String> successFiles = new ArrayList<>();
    private List<String> errorFiles = new ArrayList<>();
    private List<ScheduledFileImportResult> importResults = new ArrayList<>();

    public void setSuccessFiles(List<String> successFiles) {
        this.successFiles = successFiles;
    }

    public void setErrorFiles(List<String> errorFiles) {
        this.errorFiles = errorFiles;
    }

    public List<String> getErrorFiles() {
        return errorFiles;
    }

    public List<ScheduledFileImportResult> getImportResults() {
        return importResults;
    }

    public void setImportResults(List<ScheduledFileImportResult> importResults) {
        this.importResults = importResults;
    }

    public List<String> getSuccessFiles() {
        return successFiles;
    }

}
