package com.cannontech.common.databaseMigration.model;

public class DatabaseMigrationContainer {
    private int databaseMigrationId;
    private String databaseMigrationDisplay;
    
    public DatabaseMigrationContainer(int databaseMigrationId, String databaseMigrationDisplay){
        this.databaseMigrationId = databaseMigrationId;
        this.databaseMigrationDisplay = databaseMigrationDisplay;
    }
    
    public int getDatabaseMigrationId() {
        return databaseMigrationId;
    }
    public void setDatabaseMigrationId(int databaseMigrationId) {
        this.databaseMigrationId = databaseMigrationId;
    }
    
    public String getDatabaseMigrationDisplay() {
        return databaseMigrationDisplay;
    }
    public void setDatabaseMigrationDisplay(String databaseMigrationDisplay) {
        this.databaseMigrationDisplay = databaseMigrationDisplay;
    }
}