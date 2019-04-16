package com.cannontech.common.csvImport;

import java.util.List;

public class CapControlImportResult {
    private List<ImportResult> importResult;
    private String originalFileName;
    private String importType;

    public CapControlImportResult(List<ImportResult> importResult, String fileName, String importType) {
        super();
        this.importResult = importResult;
        this.originalFileName = fileName;
        this.importType = importType;
    }

    public List<ImportResult> getImportResult() {
        return importResult;
    }

    public void setImportResult(List<ImportResult> importResult) {
        this.importResult = importResult;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String fileName) {
        this.originalFileName = fileName;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

}
