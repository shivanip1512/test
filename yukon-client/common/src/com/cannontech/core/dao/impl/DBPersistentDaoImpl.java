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
import com.cannontech.yukon.BasicServerConnection;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DBPersistentDaoImpl implements DBPersistentDao
{
    private CacheDBChangeListener cacheDBChangeListener;
    private BasicServerConnection dispatchConnection;
    
    @Override
    public DBPersistent retrieveDBPersistent(LiteBase liteObject) {

        //create a DBPersistent from a liteBase object
        DBPersistent dbPersistent = null;
        if( liteObject != null) {
            dbPersistent = LiteFactory.createDBPersistent(liteObject);
            try {
                Transaction<DBPersistent> t = Transaction.createTransaction(Transaction.RETRIEVE, dbPersistent);
                dbPersistent = t.execute();
            } catch(Exception e) {
                com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
            }
        }
        return dbPersistent;
    }

    @Override
    public void performDBChange(DBPersistent item, int transactionType) throws PersistenceException {
        int dbChangeType = -1;
        
        switch(transactionType) {
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

        try {
            Transaction<DBPersistent> t = Transaction.createTransaction( transactionType, item);
            item = t.execute();
            
            //write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
            if (dbChangeType != DBChangeMsg.CHANGE_TYPE_NONE) {
                
                DBChangeMsg[] dbChangeMsgs = ((CTIDbChange)item).getDBChangeMsgs(dbChangeType);
                for (DBChangeMsg changeMsg : dbChangeMsgs) {
                    processDBChange(changeMsg);
                }
            }
        } catch( TransactionException e ) {
            throw new PersistenceException("Unable to save DBPersistent (item=" + 
                                           item + ", transactionType=" + transactionType + ")", e);
        }
    }
    
    @Override
    public void processDBChange(DBChangeMsg dbChange) {
        cacheDBChangeListener.handleDBChangeMessage(dbChange);
        dispatchConnection.queue(dbChange);
    }
    
    @Override
    public void performDBChangeWithNoMsg(List<DBPersistent> items, int transactionType) {
        
        try {
            MultiDBPersistent multiDBPersistent = new MultiDBPersistent();
            
            for (DBPersistent dbPersistentObj : items) {
                multiDBPersistent.getDBPersistentVector().add(dbPersistentObj);
            }

            Transaction<?> t = Transaction.createTransaction( transactionType, multiDBPersistent);
            t.execute();
            
        } catch( TransactionException e ) {
            throw new PersistenceException("Unable to save DBPersistent (items=" + 
                                           items + ", transactionType=" + transactionType + ")", e);
        }
    }

    public void setCacheDBChangeListener(CacheDBChangeListener cacheDBChangeListener) {
        this.cacheDBChangeListener = cacheDBChangeListener;
    }
    
    public void setDispatchConnection(BasicServerConnection dispatchConnection) {
        this.dispatchConnection = dispatchConnection;
    }
}