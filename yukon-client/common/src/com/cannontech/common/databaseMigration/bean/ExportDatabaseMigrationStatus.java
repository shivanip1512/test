package com.cannontech.common.databaseMigration.bean;

public class ExportDatabaseMigrationStatus {
    int currentCount = 0;
    int totalCount = 0;
    
    public ExportDatabaseMigrationStatus() {}
    public ExportDatabaseMigrationStatus(int totalCount) {
        this.totalCount = totalCount;
    }
    
    // Current Count
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
}