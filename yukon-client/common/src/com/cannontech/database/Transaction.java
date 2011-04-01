package com.cannontech.database;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDBPersistent;

@Deprecated
public class Transaction<E extends DBPersistent> {
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int RETRIEVE = 3;
    public static final int DELETE = 4;
    public static final int DELETE_PARTIAL = 5;
    public static final int ADD_PARTIAL = 6;

    private TransactionType transactionType;
    private IDBPersistent db = null;
    private E object;

    private Transaction(TransactionType transactionType, E obj) {
        super();
        this.transactionType = transactionType;
        this.object = obj;
    }

    private Transaction(TransactionType transactionType, E obj, String databaseAlias) {
        super();
        this.transactionType = transactionType;
        this.object = obj;
    }

    public static <R extends DBPersistent> Transaction<R> createTransaction(TransactionType transactionType, R obj) {
        return new Transaction<R>(transactionType, obj);
    }

    @Deprecated
    public static <R extends DBPersistent> Transaction<R> createTransaction(int operation, R obj) {
        TransactionType transactionType = TransactionType.getForOp(operation);
        return new Transaction<R>(transactionType, obj);
    }
    
    public static <R extends DBPersistent> Transaction<R> createTransaction(TransactionType transactionType, R obj, String databaseAlias) {
        return new Transaction<R>(transactionType, obj, databaseAlias);
    }

    private IDBPersistent getDB() {
       if (db == null) { 
          db = YukonSpringHook.getBean("dbPersistentBean", IDBPersistent.class);
       }
       return db;
    }
    
    @SuppressWarnings("unchecked")
    public E execute() throws TransactionException {
    	return (E) getDB().execute(this.transactionType, this.object );
    }
}