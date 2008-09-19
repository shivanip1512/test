/*
 * Created on Nov 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.dbchange.ChangeSequenceDecoder;
import com.cannontech.database.dbchange.ChangeSequenceStrategyEnum;
import com.cannontech.database.dbchange.ChangeTypeEnum;
import com.cannontech.database.dbchange.DbChangeIdentifier;
import com.cannontech.database.dbchange.DbChangeMessageHolder;
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
    private Logger log = YukonLogManager.getLogger(DBPersistentDaoImpl.class);
    
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
            
            // get dbChangeMsgs BEFORE execute
            // this may be a delete and the dbChangeMsgs may not be retrievable after execute
            DBChangeMsg[] dbChangeMsgs = null;
            if (dbChangeType != DBChangeMsg.CHANGE_TYPE_NONE) {
                dbChangeMsgs = ((CTIDbChange)item).getDBChangeMsgs(dbChangeType);
            }
            
            // execute
            Transaction<DBPersistent> t = Transaction.createTransaction( transactionType, item);
            item = t.execute();
            
            //write the DBChangeMessage out to Dispatch since it was a Successful UPDATE
            if (dbChangeMsgs != null) {
                
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
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            final DbChangeMessageHolder messageHolder;
            if (TransactionSynchronizationManager.hasResource(this)) {
                messageHolder = (DbChangeMessageHolder) TransactionSynchronizationManager.getResource(this);
            } else {
                messageHolder = createNewMessageHolder();
            }
            messageHolder.addDbChange(dbChange);
            
        } else {
            log.debug("sending out an un synchronized DB change messages");
            dispatchConnection.queue(dbChange);
        }
    }

    private DbChangeMessageHolder createNewMessageHolder() {
        final DbChangeMessageHolder messageHolder;
        messageHolder = new DbChangeMessageHolder();
        TransactionSynchronizationManager.bindResource(this, messageHolder);
        final DBPersistentDaoImpl that = this;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                sendCollectedDbChanges(messageHolder);
            }
            
            @Override
            public void afterCompletion(int status) {
                TransactionSynchronizationManager.unbindResource(that);
            }
        });
        return messageHolder;
    }
    
    private void sendCollectedDbChanges(
            final DbChangeMessageHolder messageHolder) {
        
        List<DBChangeMsg> dbChanges = messageHolder.getDbChanges();
        int initialSize = dbChanges.size();
        dbChanges = reduceDbChangeList(dbChanges);
        if (log.isDebugEnabled()) {
            log.debug("sending out " + dbChanges.size() + " held DB change messages (was " + initialSize + ")");
        }
        for (DBChangeMsg changeMsg : dbChanges) {
            dispatchConnection.queue(changeMsg);
        }
    }
    
    private static List<DBChangeMsg> reduceDbChangeList(List<DBChangeMsg> dbChanges) {
        Map<DbChangeIdentifier, DBChangeMsg> previous = new HashMap<DbChangeIdentifier, DBChangeMsg>();
        List<DBChangeMsg> output = new ArrayList<DBChangeMsg>();
        
        for (DBChangeMsg changeMsg : dbChanges) {
            DbChangeIdentifier id = DbChangeIdentifier.createIdentifier(changeMsg);
            // check for previous
            DBChangeMsg previousMessage = previous.get(id);
            if (previousMessage == null) {
                // add to output
                output.add(changeMsg);
                // add to previous
                previous.put(id, changeMsg);
            } else {
                // get strategy for this pairing
                ChangeTypeEnum previousType = ChangeTypeEnum.createFromDbChange(previousMessage);
                ChangeTypeEnum currentType = ChangeTypeEnum.createFromDbChange(changeMsg);
                ChangeSequenceStrategyEnum strategy = ChangeSequenceDecoder.getStrategy(previousType, currentType);
                switch (strategy) {
                case KEEP_BOTH:
                    output.add(changeMsg);
                    // keeping both for output, but will use the latter for additional comparisons
                    previous.put(id, changeMsg);
                    break;
                case KEEP_FIRST: 
                    break;
                case KEEP_LAST:
                    // remove the previous message from the output
                    output.remove(previousMessage);
                    output.add(changeMsg);
                    previous.put(id, changeMsg);
                    break;
                case ERROR:
                    throw new IllegalStateException("Collected a " + previousType + " -> " + currentType + " for the same object: " + id);
                }
            }
        }
        return output;
    }

    @Override
    public void performDBChangeWithNoMsg(List<DBPersistent> items, int transactionType) {
        
        MultiDBPersistent multiDBPersistent = new MultiDBPersistent();
        
        for (DBPersistent dbPersistentObj : items) {
            multiDBPersistent.getDBPersistentVector().add(dbPersistentObj);
        }
        performDBChangeWithNoMsg(multiDBPersistent, transactionType);
    }


    @Override
    public void performDBChangeWithNoMsg(DBPersistent dbPersistent, int transactionType) {
        
        try {
            Transaction<?> t = Transaction.createTransaction( transactionType, dbPersistent);
            t.execute();
            
        } catch( TransactionException e ) {
            throw new PersistenceException("Unable to save DBPersistent (dbpersistent=" + 
                                           dbPersistent + ", transactionType=" + transactionType + ")", e);
        }
    }

    public void setDispatchConnection(BasicServerConnection dispatchConnection) {
        this.dispatchConnection = dispatchConnection;
    }
}