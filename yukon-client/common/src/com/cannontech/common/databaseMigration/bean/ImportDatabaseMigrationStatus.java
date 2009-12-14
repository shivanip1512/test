package com.cannontech.common.databaseMigration.bean;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ImportDatabaseMigrationStatus {
    int currentCount = 0;
    int totalCount = 0;
    
    List<String> labelList = Lists.newArrayList();
    Map<String, List<String>> warningsMap = Maps.newLinkedHashMap();
    Map<String, List<String>> errorsMap = Maps.newLinkedHashMap();

    public ImportDatabaseMigrationStatus() {}
    public ImportDatabaseMigrationStatus(int totalCount) {
        this.totalCount = totalCount;
    }
    
    // Current Count
    public void incrementProcessed() {
        currentCount++;
    }
    public int getCurrentCount() {
        return currentCount;
    }
    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }
    
    // Total Count
    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    // Label List
    public void addLabelListEntry(String label) {
        this.labelList.add(label);
    }
    public List<String> getLabelList() {
        return labelList;
    }
    public void setLabelList(List<String> labelList) {
        this.labelList = labelList;
    }
    
    // Warnings Map
    public void addWarningListEntry(String label, String warningMessage) {
        List<String> warningList = this.warningsMap.get(label);
        if (warningList == null) {
            warningList = Lists.newArrayList();
            this.warningsMap.put(label, warningList);
        }
        warningList.add(warningMessage);
    }
    public Map<String, List<String>> getWarningsMap() {
        return warningsMap;
    }
    public void setWarningsMap(Map<String, List<String>> warningsMap) {
        this.warningsMap = warningsMap;
    }
    
    // Errors Map
    public void addErrorListEntry(String label, String errorMessage) {
        List<String> errorList = this.errorsMap.get(label);
        if (errorList == null) {
            errorList = Lists.newArrayList();
            this.errorsMap.put(label, errorList);
        }
        errorList.add(errorMessage);
    }
    public Map<String, List<String>> getErrorsMap() {
        return errorsMap;
    }
    public void setErrorsMap(Map<String, List<String>> errorsMap) {
        this.errorsMap = errorsMap;
    }
}