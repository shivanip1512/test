package com.cannontech.common.databaseMigration;

public interface TableChangeCallback {
    
    public void rowInserted(int primaryKey);

    public void rowUpdated(int primaryKey);

    public void rowDeleted(int primaryKey);

}
