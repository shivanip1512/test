package com.cannontech.common.databaseMigration.bean;

import java.io.File;
import java.util.UUID;

import com.cannontech.common.databaseMigration.model.ExportTypeEnum;
import com.cannontech.common.util.Completable;

public class ExportDatabaseMigrationStatus implements Completable {
	
	String id = null;
    int currentCount = 0;
    int totalCount = 0;
    ExportTypeEnum exportTypeEnum;
    File exportFile = null;
    
    public ExportDatabaseMigrationStatus(int totalCount, File exportFile, ExportTypeEnum exportType) {
        this.id = UUID.randomUUID().toString();
        this.totalCount = totalCount;
        this.exportTypeEnum = exportType;
        this.exportFile = exportFile;
    }
    
    public void addCurrentCount() {
    	this.currentCount++;
    }
    public String getId() {
		return id;
	}
    public int getCurrentCount() {
        return currentCount;
    }
    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }
    public int getTotalCount() {
        return totalCount;
    }
    public File getExportFile() {
		return exportFile;
	}
    public ExportTypeEnum getExportTypeEnum() {
        return exportTypeEnum;
    }
    
    @Override
    public boolean isComplete() {
    	return this.totalCount == this.currentCount;
    }
}