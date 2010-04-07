package com.cannontech.common.databaseMigration.bean;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.cannontech.common.databaseMigration.model.ExportTypeEnum;
import com.cannontech.common.util.Completable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ImportDatabaseMigrationStatus implements Completable {
    
	private String id = null;
	private File importFile = null;
	private int currentCount = 0;
	private int totalCount = 0;
	private Date startTime = null;
	private Date stopTime = null;
	private ExportTypeEnum exportType = null;
    
	private List<String> labelList = Lists.newArrayList();
	private WarningProcessingEnum warningProcessing = WarningProcessingEnum.VALIDATE;
	private Map<String, Set<String>> warningsMap = Maps.newLinkedHashMap();
	private Map<String, Set<String>> errorsMap = Maps.newLinkedHashMap();

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
        Set<String> warningList = this.warningsMap.get(label);
        if (warningList == null) {
            warningList = Sets.newHashSet();
            this.warningsMap.put(label, warningList);
        }
        warningList.add(warningMessage);
    }
    public Map<String, Set<String>> getWarningsMap() {
        return warningsMap;
    }
    public int getWarningCount() {
    	return getWarningsMap().size();
    }
    public void setWarningsMap(Map<String, Set<String>> warningsMap) {
        this.warningsMap = warningsMap;
    }
    
    // Errors Map
    public void addErrorListEntry(String label, String errorMessage) {
        Set<String> errorList = this.errorsMap.get(label);
        if (errorList == null) {
            errorList = Sets.newHashSet();
            this.errorsMap.put(label, errorList);
        }
        errorList.add(errorMessage);
    }
    public Map<String, Set<String>> getErrorsMap() {
        return errorsMap;
    }
    public int getErrorCount() {
    	return getErrorsMap().size();
    }
    public void setErrorsMap(Map<String, Set<String>> errorsMap) {
        this.errorsMap = errorsMap;
    }
    
    // Export Type
    public ExportTypeEnum getExportType() {
        return exportType;
    }
    public void setExportType(ExportTypeEnum exportType) {
        this.exportType = exportType;
    }

    public void complete() {
    	this.stopTime = new Date();
    }
    
    @Override
    public boolean isComplete() {
    	return this.currentCount == this.totalCount;
    }
    
}