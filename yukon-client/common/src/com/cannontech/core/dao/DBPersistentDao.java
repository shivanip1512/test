package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.db.DBPersistent;

public interface DBPersistentDao {

    public DBPersistent retrieveDBPersistent(LiteBase liteObject);

    /**
     * Create a transactionType Transaction and execute.
     * Write a DBChangeMsg for database corresponding to the transactionType
     * @param item
     * @param transactionType
     * @throws PersistenceException
     */
    public void performDBChange(DBPersistent item, TransactionType transactionType) throws PersistenceException;

    @Deprecated
    public void performDBChange(DBPersistent item, int transactionType) throws PersistenceException;

    /**
     * Create a transactionType Transaction and execute.
     * This method does NOT write a DBChange message.
     * @param items
     * @param transactionType
     */
    public void performDBChangeWithNoMsg(List<DBPersistent> items, TransactionType transactionType);

    /**
     * Create a transactionType Transaction and execute.
     * This method does NOT write a DBChange message.
     * @param items
     * @param transactionType
     */
    public void performDBChangeWithNoMsg(DBPersistent dbPersistent, TransactionType transactionType);

    /**
     * Retrieve a DBPersistent object, must set device id first.
     * @param dbPersistent
     */
    public DBPersistent retrieveDBPersistent(DBPersistent dbPersistent);
}