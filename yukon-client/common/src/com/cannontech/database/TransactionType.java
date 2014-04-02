package com.cannontech.database;

import org.apache.commons.lang3.Validate;

import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum TransactionType {

    INSERT (Transaction.INSERT, DbChangeType.ADD),
    UPDATE (Transaction.UPDATE, DbChangeType.UPDATE),
    RETRIEVE (Transaction.RETRIEVE, DbChangeType.NONE),
    DELETE (Transaction.DELETE, DbChangeType.DELETE),
    DELETE_PARTIAL (Transaction.DELETE_PARTIAL, DbChangeType.NONE),
    ADD_PARTIAL (Transaction.ADD_PARTIAL, DbChangeType.NONE),
    ;
    
    private final int operation;
    private final DbChangeType dbChangeType;
    
    private final static ImmutableMap<Integer, TransactionType> lookupByOp;
    
    static {
        Builder<Integer, TransactionType> opBuilder = ImmutableMap.builder();
        for (TransactionType transactionType : values()) {
            opBuilder.put(transactionType.getOperation(), transactionType);
        }
        lookupByOp = opBuilder.build();
    }
    
    private TransactionType(int operation, DbChangeType dbChangeType) {
        this.operation = operation;
        this.dbChangeType = dbChangeType;
    }
    
    public int getOperation() {
        return operation;
    }

    public DbChangeType getDbChangeType() {
        return dbChangeType;
    }

    /**
     * Looks up the Transaction operation constant.
     * @param operation
     * @return
     * @throws IllegalArgumentException
     */
    public static TransactionType getForOp(int operation) throws IllegalArgumentException {
        TransactionType transactionType = lookupByOp.get(operation);
        Validate.notNull(transactionType, Integer.toString(operation));
        return transactionType;
    }
}