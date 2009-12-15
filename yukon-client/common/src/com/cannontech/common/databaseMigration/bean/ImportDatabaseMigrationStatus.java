package com.cannontech.common.databaseMigration.bean;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cannontech.common.util.Completable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ImportDatabaseMigrationStatus implements Completable {
    
	String id = null;
	File importFile = null;
	int currentCount = 0;
    int totalCount = 0;
    Date startTime = null;
    Date stopTime = null;
    
    List<String> labelList = Lists.newArrayList();
    WarningProcessingEnum warningProcessing = WarningProcessingEnum.VALIDATE;
    Map<String, List<String>> warningsMap = Maps.newLinkedHashMap();
    Map<String, List<String>> errorsMap = Maps.newLinkedHashMap();

    public ImportDatabaseMigrationStatus(int totalCount, File importFile) {
    	this.totalCount = totalCount;
    	this.id = UUID.randomUUID().toString();
    	this.startTime = new Date();
    	this.importFile = importFile;
    }
    
    // ID
    public String getId() {
		return id;
	}
    
    // start/stop
    public Date getStartTime() {
		return startTime;
	}
    public Date getStopTime() {
		return stopTime;
	}
    
    // file
    public File getImportFile() {
		return importFile;
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
    public int getLabelCount() {
    	return getLabelList().size();
    }
    public void setLabelList(List<String> labelList) {
        this.labelList = labelList;
    }

    // Warning Processing
    public WarningProcessingEnum getWarningProcessing() {
        return warningProcessing;
    }
    public void setWarningProcessing(WarningProcessingEnum warningProcessing) {
        this.warningProcessing = warningProcessing;
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
    public int getWarningCount() {
    	return getWarningsMap().size();
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
    public int getErrorCount() {
    	return getErrorsMap().size();
    }
    public void setErrorsMap(Map<String, List<String>> errorsMap) {
        this.errorsMap = errorsMap;
    }
    
    public void complete() {
    	this.stopTime = new Date();
    }
    
    @Override
    public boolean isComplete() {
    	return this.currentCount == this.totalCount;
    }
}