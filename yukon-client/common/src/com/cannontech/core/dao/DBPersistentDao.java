package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

public interface DBPersistentDao {

    public DBPersistent retrieveDBPersistent(LiteBase liteObject);

    /**
     * Create a transactionType Transaction and execute.
     * Write a DBChangeMsg for database corresponding to the transactionType
     * @param item
     * @param transactionType
     * @throws PersistenceException
     */
    public void performDBChange(DBPersistent item, int transactionType) throws PersistenceException;

    /**
     * Create a transactionType Transaction and execute.
     * This method does NOT write a DBChange message.
     * @param items
     * @param transactionType
     */
    public void performDBChangeWithNoMsg(List<DBPersistent> items, int transactionType);

    /**
     * Create a transactionType Transaction and execute.
     * This method does NOT write a DBChange message.
     * @param items
     * @param transactionType
     */
    public void performDBChangeWithNoMsg(MultiDBPersistent multiDBPersistent, int transactionType);

    /**
     * Process a db change message
     * Queues the DBChange message to dispatch.
     * @param dbChange - Change to process
     */
    public void processDBChange(DBChangeMsg dbChange);
}