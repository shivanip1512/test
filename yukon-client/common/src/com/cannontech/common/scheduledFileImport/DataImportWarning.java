package com.cannontech.common.scheduledFileImport;

public class DataImportWarning {

    private String taskName;
    private String filesWithError;
    private Integer successFileCount;
    private String importType;

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getFilesWithError() {
        return filesWithError;
    }

    public void setFilesWithError(String filesWithError) {
        this.filesWithError = filesWithError;
    }

    public Integer getSuccessFileCount() {
        return successFileCount;
    }

    public void setSuccessFileCount(Integer successFileCount) {
        this.successFileCount = successFileCount;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}
