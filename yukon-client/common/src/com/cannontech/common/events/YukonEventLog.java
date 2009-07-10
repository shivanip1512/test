package com.cannontech.common.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface YukonEventLog {

    ExecutorTransactionality transactionality() default ExecutorTransactionality.TRANSACTIONAL;
    String category();

}
