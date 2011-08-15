package com.cannontech.common.events.loggers;

import org.joda.time.Instant;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface SystemEventLogService {

/* Usernames */
    // Username
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.login")
    public void loginPasswordChangeAttemptedByConsumer(@Arg(ArgEnum.username) LiteYukonUser user);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.login")
    public void loginUsernameChangeAttemptedByConsumer(@Arg(ArgEnum.username) LiteYukonUser user, 
                                                       @Arg(ArgEnum.username) String newUsername);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.login")
    public void loginChangeAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user, 
                                               @Arg(ArgEnum.username) String username);
    
    
    // Username service level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void usernameChanged(@Arg(ArgEnum.username) LiteYukonUser user, 
                                @Arg(ArgEnum.username) String oldUsername,
                                @Arg(ArgEnum.username) String newUsername);
    
/* Logging in */    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginWeb(@Arg(ArgEnum.username) LiteYukonUser user, 
                         @Arg(ArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginClient(@Arg(ArgEnum.username) LiteYukonUser user,
                            @Arg(ArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginInboundVoice(@Arg(ArgEnum.username) LiteYukonUser user,
                                  @Arg(ArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginOutboundVoice(@Arg(ArgEnum.username) LiteYukonUser user,
                                   @Arg(ArgEnum.remoteAddress) String remoteAddress);    
    
/* System Admin */
    /* Maintenance */
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void rphDeleteDuplicates(int rowsDeleted, Instant start, Instant finish);
    

}
