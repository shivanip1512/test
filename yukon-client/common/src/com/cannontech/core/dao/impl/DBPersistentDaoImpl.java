/*
 * Created on Nov 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.core.dao.impl;

import java.util.List;

import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.CacheDBChangeListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DBPersistentDaoImpl implements DBPersistentDao
{
    private IDatabaseCache databaseCache;
    private CacheDBChangeListener cacheDBChangeListener;
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.DBPersistentDao#retrieveDBPersistent(com.cannontech.database.data.lite.LiteBase)
     */
    public DBPersistent retrieveDBPersistent(LiteBase liteObject)
    {
        //create a DBPersistent from a liteBase object
        DBPersistent dbPersistent = null;
        if( liteObject != null)
        {
            dbPersistent = LiteFactory.createDBPersistent(liteObject);
            try {
                Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, dbPersistent);
                dbPersistent = t.execute();
            }
            catch(Exception e) {
                com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
            }
        }
        return dbPersistent;
    }
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.DBPersistentDao#performDBChange(com.cannontech.database.db.DBPersistent, com.cannontech.yukon.IServerConnection, int)
     */
    public void performDBChange(DBPersistent item, IServerConnection connToDispatch, int transactionType)
    {
        int dbChangeType = -1;
        
        switch(transactionType)
        {
            case Transaction.INSERT:
                dbChangeType = DBChangeMsg.CHANGE_TYPE_ADD;
                break;
            case Transaction.DELETE:
                dbChangeType = DBChangeMsg.CHANGE_TYPE_DELETE;
                break;
            case Transaction.UPDATE:
                dbChangeType = DBChangeMsg.CHANGE_TYPE_UPDATE;
                break;
            default:
                dbChangeType = DBChangeMsg.CHANGE_TYPE_NONE;
        }

        try
        {
            Transaction t = Transaction.createTransaction( transactionType, item);
            item = t.execute();
            
            //write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
            if (dbChangeType != DBChangeMsg.CHANGE_TYPE_NONE)
            {
                DBChangeMsg[] dbChange = databaseCache.createDBChangeMessages((CTIDbChange)item, dbChangeType);
                
                for( int i = 0; i < dbChange.length; i++)
                {
                    processDBChange(connToDispatch, dbChange[i]);
                }
            }
        }
        catch( TransactionException e )
        {
            throw new PersistenceException("Unable to save DBPersistent (item=" + 
                                           item + ", transactionType=" + transactionType + ")", e);
        }
    }
    
    public void processDBChange(DBChangeMsg dbChange) {
        IServerConnection dispatchConn = ConnPool.getInstance().getDefDispatchConn();
        this.processDBChange(dispatchConn, dbChange);
    }

    private void processDBChange(IServerConnection connToDispatch, DBChangeMsg dbChange) {
        cacheDBChangeListener.handleDBChangeMessage(dbChange);
        connToDispatch.queue(dbChange);
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.DBPersistentDao#performDBChange(com.cannontech.database.db.DBPersistent, int)
     */
    public void performDBChange(DBPersistent item, int transactionType) {
        //TODO maybe fix the type mismatch later
        IServerConnection dispatchConn = ConnPool.getInstance().getDefDispatchConn();
        performDBChange(item, dispatchConn, transactionType);
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.DBPersistentDao#performDBChange(com.cannontech.database.db.DBPersistent, com.cannontech.yukon.IServerConnection, int)
     */
    public void performDBChangeWithNoMsg(List<DBPersistent> items, int transactionType)
    {
        try
        {
            MultiDBPersistent objectsToDelete = new MultiDBPersistent();
            
            for (DBPersistent dbPersistentObj : items) {
                objectsToDelete.getDBPersistentVector().add(dbPersistentObj);
            }

            Transaction t = Transaction.createTransaction( transactionType, objectsToDelete);
            t.execute();

            
        }
        catch( TransactionException e )
        {
            throw new PersistenceException("Unable to save DBPersistent (items=" + 
                                           items + ", transactionType=" + transactionType + ")", e);
        }
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
    public void setCacheDBChangeListener(CacheDBChangeListener cacheDBChangeListener) {
        this.cacheDBChangeListener = cacheDBChangeListener;
    }
    
}
