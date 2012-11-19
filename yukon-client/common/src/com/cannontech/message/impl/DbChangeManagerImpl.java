package com.cannontech.message.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.dbchange.ChangeSequenceDecoder;
import com.cannontech.database.dbchange.ChangeSequenceStrategyEnum;
import com.cannontech.database.dbchange.DbChangeIdentifier;
import com.cannontech.database.dbchange.DbChangeMessageHolder;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeHelper;
import com.cannontech.message.dispatch.message.DbChangeType;

public class DbChangeManagerImpl implements DbChangeManager {
    private static final Logger log = YukonLogManager.getLogger(DbChangeManagerImpl.class);
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;

    @Override
    public void processDbChange(int id, int database, String category, String objectType, DbChangeType dbChangeType) {
        DBChangeMsg dbChangeMsg = new DBChangeMsg(id, database, category, objectType, dbChangeType);
        processDbChange(dbChangeMsg);
    }
    
    @Override
    public void processDbChange(int id, int database, String category, DbChangeType dbChangeType) {
        DBChangeMsg dbChangeMsg = new DBChangeMsg(id, database, category, dbChangeType);
        processDbChange(dbChangeMsg);
    }
    
    /**
     * Process a db change message
     * Queues the DBChange message to dispatch.
     * @param dbChange - Change to process
     */
    @Override
    public void processDbChange(DBChangeMsg dbChange) {
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
    public void processPaoDbChange(YukonPao pao, DbChangeType changeType) {
        PaoIdentifier paoIdentifier = pao.getPaoIdentifier();
        
        DBChangeMsg msg = new DBChangeMsg(paoIdentifier.getPaoId(),
                                          DBChangeMsg.CHANGE_PAO_DB,
                                          paoIdentifier.getPaoType().getPaoCategory().getDbString(),
                                          paoIdentifier.getPaoType().getDbString(),
                                          changeType);
        
        processDbChange(msg);
    }
    
    @Override
    public void processDbChange(DbChangeType type, DbChangeCategory category, int primaryKey) {
        DBChangeMsg dbChangeMsg = DbChangeHelper.newDbChange(type, category, primaryKey);
        processDbChange(dbChangeMsg);
    }
    
    private DbChangeMessageHolder createNewMessageHolder() {
        final DbChangeMessageHolder messageHolder = new DbChangeMessageHolder();
        TransactionSynchronizationManager.bindResource(this, messageHolder);
        final DbChangeManager that = this;
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
    
    private void sendCollectedDbChanges(final DbChangeMessageHolder messageHolder) {
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
}
