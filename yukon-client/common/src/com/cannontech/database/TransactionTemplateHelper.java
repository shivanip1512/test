package com.cannontech.database;

import java.util.concurrent.Callable;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionTemplateHelper {
    @SuppressWarnings("unchecked")
    public static <T> T execute(TransactionTemplate template, final Callable<T> callable) {
        Object result = template.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                try {
                    return callable.call();
                } catch (Exception e) {
                    if (e instanceof RuntimeException) {
                        // reduce unneeded nesting
                        RuntimeException re = (RuntimeException) e;
                        throw re;
                    }
                    throw new RuntimeException("Unable to complete callable", e);
                }
            }
        });
        
        return (T) result;
    }
    
}
