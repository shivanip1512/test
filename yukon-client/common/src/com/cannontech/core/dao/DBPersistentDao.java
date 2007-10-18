package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.db.DBPersistent;

public interface DBPersistentDao {

    public DBPersistent retrieveDBPersistent(LiteBase liteObject);

    /**
     * Create an UPDATE Transaction and execute.
     * Write a DBChangeMsg for database CHANGE_TYPE_UPDATE
     * @param item
     */
    public void performDBChange(DBPersistent item, int transactionType);


    /**
     * Create an UPDATE Transaction and execute.
     * Write a DBChangeMsg for database CHANGE_TYPE_UPDATE
     * @param item
     */
    public void performDBChangeBulk(List<DBPersistent> items, int transactionType);
    
}