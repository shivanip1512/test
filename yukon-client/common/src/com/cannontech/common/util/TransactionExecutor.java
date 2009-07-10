package com.cannontech.common.util;


public interface TransactionExecutor {
    
    public static enum ExecutorTransactionality {
        /**
         * Run this task asynchronously. The task will not participate in 
         * any transactions defined by the caller.
         */
        ASYNCHRONOUS, 
        
        /**
         * Invoke the task directly in the callers thread. Any transaction
         * that was already present will be used to record the event.
         * 
         * If the transaction is rolled back, the task will have its
         * database work rolled back. 
         */
        TRANSACTIONAL, 
        
        /**
         * Invoke the task directly in the callers thread, but force a new
         * transaction to be used. 
         */
        FORCED;
    }


    public void execute(Runnable runnable, ExecutorTransactionality transactionality);

}
