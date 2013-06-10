package com.cannontech.common.util;

import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


public class TransactionExecutorImpl implements TransactionExecutor {
    

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

    
    @PostConstruct
    public void postInit() throws Exception {
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

}
