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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.dbchange.ChangeSequenceDecoder;
import com.cannontech.database.dbchange.ChangeSequenceStrategyEnum;
import com.cannontech.database.dbchange.DbChangeIdentifier;
import com.cannontech.database.dbchange.DbChangeMessageHolder;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeHelper;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.DbPersistentBeanFactory;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DBPersistentDaoImpl implements DBPersistentDao
{
    private Logger log = YukonLogManager.getLogger(DBPersistentDaoImpl.class);
    
    private AsyncDynamicDataSource asyncDynamicDataSource;
    
    private DbPersistentBeanFactory dbPersistentBeanFactory;
    
    @Override
    public DBPersistent retrieveDBPersistent(LiteBase liteObject) {

        //create a DBPersistent from a liteBase object
        DBPersistent dbPersistent = null;
        if( liteObject != null) {
            dbPersistent = LiteFactory.createDBPersistent(liteObject);
            try {
                dbPersistentBeanFactory.createNewDbPersistentBean().execute(TransactionType.RETRIEVE, dbPersistent);
            } catch(Exception e) {
                com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
            }
        }
        return dbPersistent;
    }

    @Deprecated
    @Override
    public void performDBChange(DBPersistent item, int transactionType) throws PersistenceException {
        TransactionType transactionTypeEnum = TransactionType.getForOp(transactionType);
        performDBChange(item, transactionTypeEnum); 
    }
    
    @Override
    public void performDBChange(DBPersistent item, TransactionType transactionType) throws PersistenceException {

        try {
            executeTransaction(item, transactionType);
            
            // Generate and process db change messages when not of type NONE
            DbChangeType dbChangeType = transactionType.getDbChangeType();
            if (dbChangeType != DbChangeType.NONE) {
                DBChangeMsg[] dbChangeMsgs = getDBChangeMsgs(item, dbChangeType);
                if (dbChangeMsgs != null) {
                    for (DBChangeMsg changeMsg : dbChangeMsgs) {
                        processDBChange(changeMsg);
                    }
                }
            }
        } catch( TransactionException e ) {
            throw new PersistenceException("Unable to save DBPersistent (item=" + 
                                           item + ", transactionType=" + transactionType + ")", e);
        }
    }

    private void executeTransaction(DBPersistent item,
            TransactionType transactionType) throws TransactionException {
    	dbPersistentBeanFactory.createNewDbPersistentBean().execute(transactionType, item);
    }

    private DBChangeMsg[] getDBChangeMsgs(DBPersistent item, DbChangeType dbChangeType) {
        DBChangeMsg[] dbChangeMsgs = null;
        if (dbChangeType != DbChangeType.NONE) {
            if(item instanceof CTIDbChange){
                dbChangeMsgs = ((CTIDbChange)item).getDBChangeMsgs(dbChangeType);
            }
        }
        return dbChangeMsgs;
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
            asyncDynamicDataSource.publishDbChange(dbChange);
        }
    }
    
    @Override
    public void processDatabaseChange(DbChangeType type, DbChangeCategory category, int primaryKey) {
        DBChangeMsg dbChangeMsg = DbChangeHelper.newDbChange(type, category, primaryKey);
        processDBChange(dbChangeMsg);
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
            asyncDynamicDataSource.publishDbChange(changeMsg);
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
                DbChangeType previousType = previousMessage.getDbChangeType();
                DbChangeType currentType = changeMsg.getDbChangeType();
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
    @Transactional
    public void performDBChangeWithNoMsg(List<DBPersistent> items, TransactionType transactionType) {
        for (DBPersistent dbPersistentObj : items) {
            performDBChangeWithNoMsg(dbPersistentObj, transactionType);
        }
    }


    @Override
    public void performDBChangeWithNoMsg(DBPersistent dbPersistent, TransactionType transactionType) {
        
        try {
        	dbPersistentBeanFactory.createNewDbPersistentBean().execute(transactionType, dbPersistent);
        } catch( TransactionException e ) {
            throw new PersistenceException("Unable to save DBPersistent (dbpersistent=" + 
                                           dbPersistent + ", transactionType=" + transactionType + ")", e);
        }
    }

    @Autowired
    public void setAsyncDynamicDataSource(AsyncDynamicDataSource asyncDynamicDataSource) {
        this.asyncDynamicDataSource = asyncDynamicDataSource;
    }
    
    @Autowired
    public void setDbPersistentBeanFactory(
			DbPersistentBeanFactory dbPersistentBeanFactory) {
		this.dbPersistentBeanFactory = dbPersistentBeanFactory;
	}
}