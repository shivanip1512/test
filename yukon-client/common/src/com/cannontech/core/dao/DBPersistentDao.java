package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

public interface DBPersistentDao {

    public DBPersistent retrieveDBPersistent(LiteBase liteObject);

    /**
     * Create an UPDATE Transaction and execute.
     * Write a DBChangeMsg for database CHANGE_TYPE_UPDATE
     * @param item
     */
    public void performDBChange(DBPersistent item, int transactionType) throws PersistenceException;


    /**
     * Create an UPDATE Transaction and execute.
     * Write a DBChangeMsg for database CHANGE_TYPE_UPDATE
     * @param item
     */
    public void performDBChangeWithNoMsg(List<DBPersistent> items, int transactionType);
    
    /**
     * Method process a db change message
     * @param dbChange - Change to process
     */
    public void processDBChange(DBChangeMsg dbChange);
}