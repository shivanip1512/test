package com.cannontech.common.databaseMigration.bean;

import java.util.ArrayList;
import java.util.List;

public class DatabaseMigrationImportCallback {
    int processed = 0;
    List<String> warningList;
    List<String> errorList;
    
    public DatabaseMigrationImportCallback(){
        this.warningList = new ArrayList<String>();
        this.errorList = new ArrayList<String>();
    }

    public void incrementProcessed(){
        this.processed++;
    }
    public int getProcessed() {
        return processed;
    }
    public void setProcessed(int processed) {
        this.processed = processed;
    }
 
    public void addWarningListEntry(String warningListEntry) {
        this.warningList.add(warningListEntry);
    }
    public List<String> getWarningList() {
        return warningList;
    }
    public void setWarningList(List<String> warningList) {
        this.warningList = warningList;
    }

    public void addErrorListEntry(String errorListEntry) {
        this.errorList.add(errorListEntry);
    }
    public List<String> getErrorList() {
        return errorList;
    }
    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }
}
