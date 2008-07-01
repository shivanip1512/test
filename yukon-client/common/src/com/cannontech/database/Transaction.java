package com.cannontech.database;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.yukon.concrete.ResourceFactory;

public class Transaction<E extends DBPersistent> {
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int RETRIEVE = 3;
    public static final int DELETE = 4;
    public static final int DELETE_PARTIAL = 5;
    public static final int ADD_PARTIAL = 6;

    private int operation;
    private com.cannontech.yukon.IDBPersistent db = null;

    private E object;

    private Transaction(int operation, E obj) {
        super();
        this.operation = operation;
        this.object = obj;
    }

    private Transaction(int operation, E obj, String databaseAlias) {
        super();
        this.operation = operation;
        this.object = obj;
    }

    public static <R extends DBPersistent> Transaction<R> createTransaction(int operation, R obj) {
        return new Transaction<R>(operation, obj);
    }

    public static <R extends DBPersistent> Transaction<R> createTransaction(int operation, R obj, String databaseAlias) {
        return new Transaction<R>(operation, obj, databaseAlias);
    }

    private com.cannontech.yukon.IDBPersistent getDB() {
        if (db == null) {
            db = ResourceFactory.getIYukon().createIDBPersistent();
        }    
        return db;
    }

    @SuppressWarnings("unchecked")
    public E execute() throws TransactionException {
        return (E) getDB().execute(this.operation, this.object );
    }
}
