package com.cannontech.common.util;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


public class TransactionExecutorImpl implements TransactionExecutor, InitializingBean {
    

    private Executor executor;
    private PlatformTransactionManager platformTransactionManager;
    
    private TransactionTemplate forcedTransactionTemplate;
    private TransactionTemplate normalTransactionTemplate;

    @Override
    @Transactional
    public void execute(final Runnable runnable, ExecutorTransactionality transactionality) {
        if (transactionality == ExecutorTransactionality.ASYNCHRONOUS) {
            executor.execute(runnable);
            return;
        }
        
        boolean forced = transactionality == ExecutorTransactionality.FORCED;
        final TransactionTemplate transactionTemplate = forced ? forcedTransactionTemplate : normalTransactionTemplate;
        
        transactionTemplate.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                runnable.run();
                return null;
            }
        });
    }

    
    @Override
    public void afterPropertiesSet() throws Exception {
        forcedTransactionTemplate = new TransactionTemplate(platformTransactionManager);
        forcedTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        
        normalTransactionTemplate = new TransactionTemplate(platformTransactionManager);
        normalTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
    }
    
    @Autowired
    public void setExecutor(@Qualifier("main")Executor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setPlatformTransactionManager(
            PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    @Autowired
    public void setForcedTransactionTemplate(
            TransactionTemplate forcedTransactionTemplate) {
        this.forcedTransactionTemplate = forcedTransactionTemplate;
    }

    @Autowired
    public void setNormalTransactionTemplate(
            TransactionTemplate normalTransactionTemplate) {
        this.normalTransactionTemplate = normalTransactionTemplate;
    }



}
