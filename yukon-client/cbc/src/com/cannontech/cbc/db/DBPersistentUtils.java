package com.cannontech.cbc.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.conns.ConnPool;

public class DBPersistentUtils {

    public static void addDBObject(DBPersistent db) throws TransactionException {

        try {
            Transaction t = Transaction.createTransaction(Transaction.INSERT,
                                                          db);
            t.execute();
        }

        catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
            throw e; // chuck this thing up
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
            throw new TransactionException(e.getMessage(), e); // chuck this
                                                                // thing up
        }

        try {
            generateDBChangeMsg(db, DBChangeMsg.CHANGE_TYPE_ADD);
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);

            throw new TransactionException(e.getMessage(), e); // chuck this
                                                                // thing up
        }
    }

    public static void generateDBChangeMsg(DBPersistent object, int changeType) {
        if (object instanceof CTIDbChange) {
            DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance()
                                                         .createDBChangeMessages((CTIDbChange) object,
                                                                                 changeType);

            for (int i = 0; i < dbChange.length; i++) {

                dbChange[i].setUserName("test user");
                DefaultDatabaseCache.getInstance()
                                    .handleDBChangeMessage(dbChange[i]);
                ConnPool.getInstance().getDefDispatchConn().write(dbChange[i]);
            }
        } else {
            throw new IllegalArgumentException("Non " + CTIDbChange.class.getName() + " class tried to generate a " + DBChangeMsg.class.getName() + " its class was : " + object.getClass()
                                                                                                                                                                                .getName());
        }

    }

    public static DBPersistent retrieveDBPersistent(DBPersistent dbObj) {

        Connection conn = null;
        if( dbObj == null ) return null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            dbObj.setDbConnection( conn );
            dbObj.retrieve();
        }
        catch( SQLException sql ) {
            CTILogger.error("Unable to retrieve DB Object", sql );
        }
        finally {
            dbObj.setDbConnection( null );
            
            try {
            if( conn != null ) conn.close();
            } catch( java.sql.SQLException e2 ) { }         
        }
        
        return dbObj;   
    }

}
