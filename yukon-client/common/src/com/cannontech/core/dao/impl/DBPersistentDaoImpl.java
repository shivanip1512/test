package com.cannontech.core.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.DbPersistentBeanFactory;

public class DBPersistentDaoImpl implements DBPersistentDao {
    private final Logger log = YukonLogManager.getLogger(DBPersistentDaoImpl.class);
    
    @Autowired private DbPersistentBeanFactory dbPersistentBeanFactory;
    @Autowired private DbChangeManager dbChangeManager;
    
    @Override
    public DBPersistent retrieveDBPersistent(LiteBase liteObject) {
        DBPersistent dbPersistent = null;
        if (liteObject != null) {
            //create a DBPersistent from a liteBase object
            dbPersistent = LiteFactory.createDBPersistent(liteObject);
            retrieveDBPersistent(dbPersistent);
        }
        return dbPersistent;
    }
    
    @Override
    public DBPersistent retrieveDBPersistent(DBPersistent dbPersistent) {
        try {
            dbPersistentBeanFactory.createNewDbPersistentBean().execute(TransactionType.RETRIEVE, dbPersistent);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
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
            DBChangeMsg[] dbChangeMsgs = getDBChangeMsgs(item, dbChangeType);
            if (dbChangeMsgs != null) {
                for (DBChangeMsg changeMsg : dbChangeMsgs) {
                    dbChangeManager.processDbChange(changeMsg);
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
        if (!(item instanceof CTIDbChange) || dbChangeType == DbChangeType.NONE) {
            return null;
        }

        return ((CTIDbChange)item).getDBChangeMsgs(dbChangeType);
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
}